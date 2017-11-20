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

import static org.junit.Assert.assertTrue;

import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notifications.test.data.NotificationData;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.impl.TransmissionHandlerImpl;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@Category(RequiresNone.class)
public class TransmissionHandlerTest {

  private static final int MAX_LIMIT = 1000;

  @InjectMocks
  private TransmissionHandlerImpl handler;

  @Mock
  private TransmissionDAO transmissionDAO;

  @Mock
  private NotificationDAO notificationDAO;

  @Mock
  private GeneralConfig generalConfig;

  private Notification note;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    note = NotificationData.newTestInstance();
  }

  @Test
  public void testFindByCreatedDuration() {
    assertTrue("Should not return any Transmissions",
        handler.findByCreatedDuration(0, Long.MAX_VALUE).isEmpty());
  }

  @Test
  public void testFindByCreatedAfter() {
    assertTrue("Should not return any Transmissions", handler.findByCreatedAfter(0).isEmpty());
  }

  @Test
  public void testFindByCreatedBefore() {
    assertTrue("Should not return any Transmissions",
        handler.findByCreatedBefore(Long.MAX_VALUE).isEmpty());
  }

  @Test
  public void testFindByNotificationSlug() {
    Mockito.when(notificationDAO.findBySlugIgnoreCase(NotificationData.TEST_SLUG)).thenReturn(note);
    assertTrue("Should not return any Transmissions",
        handler.findByNotificationSlug(NotificationData.TEST_SLUG).isEmpty());
  }

  @Test(expected = NotFoundException.class)
  public void testFindByNotificationSlugNotFound() {
    assertTrue("Should not return any Transmissions",
        handler.findByNotificationSlug(NotificationData.TEST_SLUG).isEmpty());
  }

  @Test
  public void testFindEscalatedTransmissions() {
    assertTrue("Should not return any Transmissions",
        handler.findEscalatedTransmissions().isEmpty());
  }

  @Test
  public void testFindFailedTransmissions() {
    assertTrue("Should not return any Transmissions", handler.findFailedTransmissions().isEmpty());
  }

  @Test
  public void testFindByCreatedDurationWithMaxLimit() {
    assertTrue("Should not return any Transmissions",
        handler.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindByCreatedAfterWithMaxLimit() {
    assertTrue("Should not return any Transmissions",
        handler.findByCreatedAfter(0, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindByCreatedBeforeWithMaxLimit() {
    assertTrue("Should not return any Transmissions",
        handler.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindByNotificationSlugWithMaxLimit() {
    Mockito.when(notificationDAO.findBySlugIgnoreCase(NotificationData.TEST_SLUG)).thenReturn(note);
    assertTrue("Should not return any Transmissions",
        handler.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT).isEmpty());
  }

  @Test(expected = NotFoundException.class)
  public void testFindByNotificationSlugWithMaxLimitNotFound() {
    assertTrue("Should not return any Transmissions",
        handler.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindEscalatedTransmissionsWithMaxLimit() {
    assertTrue("Should not return any Transmissions",
        handler.findEscalatedTransmissions(MAX_LIMIT).isEmpty());
  }

  @Test
  public void testFindFailedTransmissionsWithMaxLimit() {
    assertTrue("Should not return any Transmissions",
        handler.findFailedTransmissions(MAX_LIMIT).isEmpty());
  }

  @Test
  public void testDeleteOldSentTransmissions() {
    handler.deleteOldSentTransmissions(0);
  }

  @Test
  public void testDeleteOldAcknowledgedTransmissions() {
    handler.deleteOldAcknowledgedTransmissions(0);
  }

  @Test
  public void testDeleteOldEscalatedTransmissions() {
    handler.deleteOldEscalatedTransmissions(0);
  }

  @Test
  public void testDeleteOldFailedTransmissions() {
    handler.deleteOldFailedTransmissions(0);
  }

}
