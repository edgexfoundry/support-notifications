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

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notification.test.data.NotificationData;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.impl.NormalSeverityDistributionExecutor;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@Category(RequiresNone.class)
public class NormalSeverityDistributionExecutorTest {

  @InjectMocks
  private NormalSeverityDistributionExecutor executor;

  @Mock
  private NotificationDAO notificationDAO;

  @Mock
  private DistributionCoordinator distributionCoordinator;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testStartDistributing() {
    executor.startDistributing();
  }

  @Test
  public void testStartDistributingWithNotifiations() {
    Notification note = new Notification();
    note = NotificationData.newTestInstance();
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    Mockito.when(notificationDAO.findBySeverityAndStatus(NotificationData.TEST_SEV,
        NotificationData.TEST_STATUS)).thenReturn(notes);
    executor.startDistributing();
  }

}
