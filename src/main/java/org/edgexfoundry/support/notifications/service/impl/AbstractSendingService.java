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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.support.domain.notifications.NotificationStatus;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.domain.notifications.TransmissionRecord;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;
import org.edgexfoundry.support.notifications.GlobalVariables;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.EscalationService;
import org.edgexfoundry.support.notifications.service.SendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

public abstract class AbstractSendingService implements SendingService {

  private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

  @Autowired
  private TransmissionDAO transmissionDAO;

  @Autowired
  private EscalationService escalationService;

  @Autowired
  private ThreadPoolTaskScheduler scheduler;

  @Autowired
  private GeneralConfig generalConfig;

  @Value("${application.scheduler.critical.resend.duration}")
  private long criticalResendDuration;

  @Async
  @Override
  public void send(Notification notification, Channel channel, String receiver) {
    this.checkParameters(notification, channel);

    TransmissionRecord record = sendToReceiver(notification, channel);
    Transmission transmission = new Transmission();
    transmission.setNotification(notification);
    transmission.setReceiver(receiver);
    transmission.setChannel(channel);
    transmission.setResendCount(0);
    transmission.setStatus(record.getStatus());
    transmission.setRecords(new TransmissionRecord[] {record});

    try {
      transmission = transmissionDAO.insert(transmission);
    } catch (Exception e) {
      logger.error("transmission cannot be persisted: " + transmission.toString());
      logger.error(e.getMessage(), e);
      throw new ServiceException(e);
    }

    handleFailedTransmission(transmission);
  }

  @Async
  @Override
  public void resend(Transmission transmission) {
    this.checkParameters(transmission);

    TransmissionRecord record =
        sendToReceiver(transmission.getNotification(), transmission.getChannel());
    transmission.inreaseResendCount();
    transmission.setStatus(record.getStatus());
    transmission.setRecords(addNewRecordToArray(transmission.getRecords(), record));

    try {
      transmission = transmissionDAO.save(transmission);
    } catch (Exception e) {
      logger.error("transmission cannot be updated: " + transmission.toString());
      logger.error(e.getMessage(), e);
      throw new ServiceException(e);
    }

    handleFailedTransmission(transmission);
  }
  
  private void handleFailedTransmission(Transmission transmission) {
    Notification notification = transmission.getNotification();
    if (transmission.getStatus() == TransmissionStatus.FAILED
        && notification.getStatus() != NotificationStatus.ESCALATED) {
      if (notification.getSeverity() == NotificationSeverity.CRITICAL) {
        if (transmission.getResendCount() < generalConfig.getResendLimit()) {
          scheduler.schedule(new CriticalSeverityResendTask(transmission, this),
              new Date(System.currentTimeMillis() + criticalResendDuration));
        } else {
          doEscalation(transmission);
        }
      } else if (notification.getSeverity() == NotificationSeverity.NORMAL) {
        GlobalVariables.getInstance()
            .setTheLatestNormalTransmissionFailed(transmission.getModified());
      }
    }
  }

  private void doEscalation(Transmission transmission) {
    logger.warn("This transmission is escalated: " + transmission);
    escalationService.escalate(transmission);

    transmission.setStatus(TransmissionStatus.ESCALATED);
    transmissionDAO.save(transmission);
  }

  protected void checkParameters(Notification notification, Channel channel) {
    if (notification == null) {
      logger.error(this.getClass().toString() + " received a null notification");
      throw new DataValidationException("Notification is null");
    }

    if (channel == null) {
      logger.error(this.getClass().toString() + " got a null channel");
      throw new DataValidationException("Channel is null");
    }
  }


  private void checkParameters(Transmission transmission) {
    if (transmission == null) {
      logger.error(this.getClass().toString() + " received a null transmission");
      throw new DataValidationException("Transmission is null");
    }

    this.checkParameters(transmission.getNotification(), transmission.getChannel());
  }

  abstract TransmissionRecord sendToReceiver(Notification notification, Channel channel);

  private TransmissionRecord[] addNewRecordToArray(TransmissionRecord[] originalRecords,
      TransmissionRecord newRecord) {
    List<TransmissionRecord> newRecords = new ArrayList<>();
    for (TransmissionRecord r : originalRecords) {
      newRecords.add(r);
    }
    newRecords.add(newRecord);
    return newRecords.stream().toArray(TransmissionRecord[]::new);
  }

}
