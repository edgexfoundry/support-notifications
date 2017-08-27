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

package org.edexfoundry.support.notifications.service;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notification.test.data.NotificationData;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.SubscriptionDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.impl.EscalationServiceImpl;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Category(RequiresNone.class)
public class EscalationServiceTest {

  @InjectMocks
  private EscalationServiceImpl service;

  @Mock
  private DistributionCoordinator distributionCoordinator;

  @Mock
  private NotificationDAO notificationDAO;

  @Mock
  private SubscriptionDAO subscriptionDAO;

  private Transmission trans;
  private Notification note;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    trans = new Transmission();
    note = NotificationData.newTestInstance();
    trans.setNotification(note);
  }

  @Test
  public void testEscalate() {
    service.escalate(trans);
  }

  @Test(expected = DataValidationException.class)
  public void testEscalateWithNull() {
    service.escalate(null);
  }

  @Test(expected = ServiceException.class)
  public void testEscalateServiceException() {
    trans.setNotification(null);
    service.escalate(trans);
  }

}
