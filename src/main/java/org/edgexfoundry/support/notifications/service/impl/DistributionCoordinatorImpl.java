/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-notifications
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.notifications.service.impl;

import java.util.List;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.ChannelType;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.dao.SubscriptionDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.SendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DistributionCoordinatorImpl implements DistributionCoordinator {

  private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

  @Autowired
  private SubscriptionDAO subscriptionDAO;

  @Autowired
  @Qualifier("RESTfulSendingService")
  private SendingService restfulSendingService;

  @Autowired
  @Qualifier("EMAILSendingService")
  private SendingService emailSendingService;

  @Async
  @Override
  public void distribute(Notification notification) {
    if (notification == null) {
      logger.error("DistributionCoordinator received a null object");
      throw new DataValidationException("Notification is null");
    }

    logger.debug(
        "DistributionCoordinator start distributing notification: " + notification.toString());

    List<Subscription> subscriptions;
    String[] category = notification.getCategory() == null ? new String[] {}
        : new String[] {notification.getCategory().toString()};
    String[] labels = notification.getLabels() == null ? new String[] {} : notification.getLabels();
    try {
      subscriptions = subscriptionDAO
          .findBySubscribedCategoriesInIgnoreCaseOrSubscribedLabelsInIgnoreCase(category, labels);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ServiceException(e);
    }

    for (Subscription subscription : subscriptions) {
      logger.debug("found subscription: " + subscription.toString());
      sendViaChannel(notification, subscription);
    }
  }

  @Override
  public void sendViaChannel(Notification notification, Subscription subscription) {
    for (Channel channel : subscription.getChannels()) {
      sendViaChannel(notification, channel, subscription.getReceiver());
    }
  }

  @Override
  public void sendViaChannel(Notification notification, Channel channel, String receiver) {
    if (notification == null) {
      logger.error("DistributionCoordinator received a null notification");
      throw new DataValidationException("notification is null");
    }

    logger.debug("sending notification slug=" + notification.getSlug() + " to chanel: "
        + channel.toString());
    if (channel.getType() == ChannelType.REST) {
      restfulSendingService.send(notification, channel, receiver);
    } else if (channel.getType() == ChannelType.EMAIL) {
      emailSendingService.send(notification, channel, receiver);
    }
  }

  @Override
  public void resendViaChannel(Transmission transmission) {
    if (transmission == null) {
      logger.error("DistributionCoordinator received a null transmission");
      throw new DataValidationException("transmission is null");
    }

    Channel channel = transmission.getChannel();
    if (channel == null) {
      logger.error("DistributionCoordinator received a null channel");
      throw new DataValidationException("channel is null");
    }

    logger.debug("resending transmission: " + transmission.toString());
    if (channel.getType() == ChannelType.REST) {
      restfulSendingService.resend(transmission);
    } else if (channel.getType() == ChannelType.EMAIL) {
      emailSendingService.resend(transmission);
    }
  }

}
