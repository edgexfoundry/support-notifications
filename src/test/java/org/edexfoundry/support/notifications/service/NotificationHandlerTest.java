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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.support.notification.test.data.NotificationData;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.impl.NotificationHandlerImpl;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;

@Category(RequiresNone.class)
public class NotificationHandlerTest {


  private static final int MAX_LIMIT = 10;

  @InjectMocks
  private NotificationHandlerImpl handler;

  @Mock
  private NotificationDAO notificationDAO;

  @Mock
  private TransmissionDAO transmissionDAO;

  @Mock
  private DistributionCoordinator distributionCoordinator;

  private Notification note = new Notification();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    note = NotificationData.newTestInstance();
  }

  @Test
  public void testReceiveNotification() {
    Mockito.when(notificationDAO.insert(note)).thenReturn(note);
    handler.receiveNotification(note);
  }

  @Test(expected = DataValidationException.class)
  public void testReceiveNotificationDuplicateKey() {
    Mockito.when(notificationDAO.insert(note)).thenThrow(new DuplicateKeyException("test"));
    handler.receiveNotification(note);
  }

  @Test
  public void testReceiveNotificationCritical() {
    note.setSeverity(NotificationSeverity.CRITICAL);
    Mockito.when(notificationDAO.insert(note)).thenReturn(note);
    Mockito.when(notificationDAO.save(note)).thenReturn(note);
    handler.receiveNotification(note);
  }

  @Test
  public void testReceiveNotificationNormal() {
    note.setSeverity(NotificationSeverity.NORMAL);
    Mockito.when(notificationDAO.insert(note)).thenReturn(note);
    Mockito.when(notificationDAO.save(note)).thenReturn(note);
    handler.receiveNotification(note);
  }

  @Test(expected = ClientException.class)
  public void testReceiveNotificationWithNull() {
    handler.receiveNotification(null);
  }

  @Test
  public void testFindBySlug() {
    assertNull("Did not return null on find by", handler.findBySlug(NotificationData.TEST_SLUG));
  }

  @Test
  public void testSearchBySender() {
    assertTrue("Did not return emty list on find by",
        handler.searchBySender(NotificationData.TEST_SENDER).isEmpty());
  }

  @Test
  public void testSearchBySenderMaxLimit() {
    assertTrue("Did not return emty list on find by",
        handler.searchBySender(NotificationData.TEST_SENDER, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindByCreatedDuration() {
    assertTrue("Should not return any Notifications",
        handler.findByCreatedDuration(0, Long.MAX_VALUE).isEmpty());
  }

  @Test
  public void testFindByCreatedDurationWithLimit() {
    assertTrue("Should not return any Notifications",
        handler.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindByCreatedAfter() {
    assertTrue("Should not return any Notifications", handler.findByCreatedAfter(0).isEmpty());
  }

  @Test
  public void testFindByCreatedAfterWithLimit() {
    assertTrue("Should not return any Notifications",
        handler.findByCreatedAfter(0, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindByCreatedBefore() {
    assertTrue("Should not return any Notifications",
        handler.findByCreatedBefore(Long.MAX_VALUE).isEmpty());
  }

  @Test
  public void testFindByCreatedBeforeWithLimit() {
    assertTrue("Should not return any Notifications",
        handler.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindInLabels() {
    assertTrue("Should not return any Notifications",
        handler.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindInLabelsWithLimits() {
    assertTrue("Should not return any Notifications",
        handler.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindNewNotifications() {
    assertTrue("Should not return any Notifications", handler.findNewNotifications().isEmpty());
  }

  @Test
  public void testFindNewNotificationsWithLimits() {
    assertTrue("Should not return any Notifications",
        handler.findNewNotifications(MAX_LIMIT).isEmpty());
  }

  @Test
  public void testDeleteNotificationAndTransmissionsBySlug() {
    Mockito.when(notificationDAO.findBySlugIgnoreCase(NotificationData.TEST_SLUG)).thenReturn(note);
    handler.deleteNotificationAndTransmissionsBySlug(NotificationData.TEST_SLUG);
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteNotificationAndTransmissionsBySlugNotFound() {
    handler.deleteNotificationAndTransmissionsBySlug(NotificationData.TEST_SLUG);
  }

  @Test(expected = ServiceException.class)
  public void testDeleteNotificationAndTransmissionsBySlugException() {
    Mockito.when(notificationDAO.findBySlugIgnoreCase(NotificationData.TEST_SLUG))
        .thenThrow(new RuntimeException());
    handler.deleteNotificationAndTransmissionsBySlug(NotificationData.TEST_SLUG);
  }

  @Test(expected = ClientException.class)
  public void testDeleteNotificationAndTransmissionsBySlugWithNull() {
    handler.deleteNotificationAndTransmissionsBySlug(null);
  }

  @Test
  public void testDeleteOldProcessedNotificationsAndTransmissions() {
    handler.deleteOldProcessedNotificationsAndTransmissions(0);
  }


}
