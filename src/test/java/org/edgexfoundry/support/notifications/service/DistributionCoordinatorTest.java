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
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.notifications.service;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.ChannelType;
import org.edgexfoundry.support.domain.notifications.EmailChannel;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationCategory;
import org.edgexfoundry.support.domain.notifications.RESTfulChannel;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.dao.SubscriptionDAO;
import org.edgexfoundry.support.notifications.service.impl.DistributionCoordinatorImpl;
import org.edgexfoundry.support.notifications.service.impl.EMAILSendingService;
import org.edgexfoundry.support.notifications.service.impl.RESTfulSendingService;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@Category(RequiresNone.class)
public class DistributionCoordinatorTest {

  @InjectMocks
  private DistributionCoordinatorImpl coordinator;

  @Mock
  private SubscriptionDAO dao;

  @Mock
  private RESTfulSendingService restfulSendingService;

  @Mock
  private EMAILSendingService emailSendingService;

  private Notification note;

  private Subscription sub;

  private Channel channel;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    note = new Notification();
    sub = new Subscription();
    channel = new EmailChannel();
    channel.setType(ChannelType.EMAIL);
    Channel[] channels = {channel};
    sub.setChannels(channels);
  }

  @Test
  public void distribute() {
    coordinator.distribute(note);
  }

  @Test
  public void distributeWithCategoriesAndLabels() {
    note.setCategory(NotificationCategory.SW_HEALTH);
    note.setLabels(new String[] {"label1", "label2"});
    coordinator.distribute(note);
  }

  @Test
  public void distributeTestDaoSubscriptions() {
    String[] labels = {"label1", "label2"};
    note.setLabels(labels);
    note.setCategory(NotificationCategory.SW_HEALTH);
    List<Subscription> subs = new ArrayList<>();
    subs.add(sub);
    Mockito.when(dao.findBySubscribedCategoriesInIgnoreCaseOrSubscribedLabelsInIgnoreCase(
        new String[] {"SW_HEALTH"}, labels)).thenReturn(subs);
    coordinator.distribute(note);
  }

  @Test(expected = ServiceException.class)
  public void distributeTestDaoError() {
    String[] labels = {"label1", "label2"};
    note.setLabels(labels);
    note.setCategory(NotificationCategory.SW_HEALTH);
    Mockito.when(dao.findBySubscribedCategoriesInIgnoreCaseOrSubscribedLabelsInIgnoreCase(
        new String[] {"SW_HEALTH"}, labels)).thenThrow(new RuntimeException("test"));
    coordinator.distribute(note);
  }

  @Test(expected = DataValidationException.class)
  public void distributeWithNullNotification() {
    coordinator.distribute(null);
  }

  @Test
  public void testSendViaChannelEmail() {
    coordinator.sendViaChannel(note, sub);
  }

  @Test
  public void testSendViaChannelREST() {
    channel = new RESTfulChannel();
    channel.setType(ChannelType.REST);
    Channel[] channels = {channel};
    sub.setChannels(channels);
    coordinator.sendViaChannel(note, sub);
  }

  @Test(expected = DataValidationException.class)
  public void testSendViaChannelWithNull() {
    coordinator.sendViaChannel(null, sub);
  }

  @Test
  public void testResendViaChannel() {
    Transmission trans = new Transmission();
    trans.setChannel(channel);
    coordinator.resendViaChannel(trans);
  }

  @Test(expected = DataValidationException.class)
  public void testResendViaChannelNullTrans() {
    coordinator.resendViaChannel(null);
  }

  @Test(expected = DataValidationException.class)
  public void testResendViaChannelNullChannel() {
    Transmission trans = new Transmission();
    coordinator.resendViaChannel(trans);
  }

}
