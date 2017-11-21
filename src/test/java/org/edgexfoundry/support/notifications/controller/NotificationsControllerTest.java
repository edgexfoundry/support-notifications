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

package org.edgexfoundry.support.notifications.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notifications.test.data.NotificationData;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.controller.impl.NotificationControllerImpl;
import org.edgexfoundry.support.notifications.service.NotificationHandler;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

@Category(RequiresNone.class)
public class NotificationsControllerTest {

  private static final int MAX_LIMIT = 1000;

  @InjectMocks
  private NotificationControllerImpl controller;

  @Mock
  private NotificationHandler handler;

  @Mock
  private GeneralConfig config;

  private Notification note;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    note = NotificationData.newTestInstance();
  }

  @Test
  public void testReceiveNotification() {
    assertEquals("Send of new notification did not return healthy status", HttpStatus.ACCEPTED,
        controller.receiveNotification(note).getStatusCode());
  }

  @Test(expected = ServiceException.class)
  public void testReceiveNotificationException() {
    controller = new NotificationControllerImpl();
    controller.receiveNotification(note);
  }

  @Test(expected = ClientException.class)
  public void testReceiveNotificationBadSlug() {
    note.setSlug(null);
    controller.receiveNotification(note);
  }

  @Test(expected = DataValidationException.class)
  public void testReceiveNotificationDuplicateSlug() {
    Mockito.when(handler.findBySlug(NotificationData.TEST_SLUG)).thenReturn(note);
    controller.receiveNotification(note);
  }

  @Test
  public void testFindBySlug() {
    Mockito.when(handler.findBySlug(NotificationData.TEST_SLUG)).thenReturn(note);
    assertEquals("Find by method did not return expected notification", note,
        controller.findBySlug(NotificationData.TEST_SLUG));
  }

  @Test(expected = NotFoundException.class)
  public void testFindBySlugNotFound() {
    controller.findBySlug("foo");
  }

  @Test(expected = ServiceException.class)
  public void testFindBySlugServiceException() {
    Mockito.when(handler.findBySlug(NotificationData.TEST_SLUG))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findBySlug(NotificationData.TEST_SLUG);
  }

  @Test(expected = ServiceException.class)
  public void testFindBySlugException() {
    controller = new NotificationControllerImpl();
    controller.findBySlug(NotificationData.TEST_SLUG);
  }

  @Test
  public void testFindBySender() {
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    Mockito.when(handler.searchBySender(NotificationData.TEST_SENDER, MAX_LIMIT)).thenReturn(notes);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected notifications", 1,
        controller.findBySender(NotificationData.TEST_SENDER, MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindBySenderLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findBySender(NotificationData.TEST_SENDER, MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindBySenderServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.searchBySender(NotificationData.TEST_SENDER, MAX_LIMIT))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findBySender(NotificationData.TEST_SENDER, MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindBySenderException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.searchBySender(NotificationData.TEST_SENDER, MAX_LIMIT))
        .thenThrow(new RuntimeException("test"));
    controller.findBySender(NotificationData.TEST_SENDER, MAX_LIMIT);
  }

  @Test
  public void testFindByCreatedDuration() {
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    Mockito.when(handler.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT)).thenReturn(notes);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected notifications", 1,
        controller.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedDurationLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindByCreatedDurationServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT))
        .thenThrow(new ServiceException(new RuntimeException("test")));
    controller.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindByCreatedDurationException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT))
        .thenThrow(new RuntimeException("test"));
    controller.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test
  public void testFindByCreatedAfter() {
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    Mockito.when(handler.findByCreatedAfter(0, MAX_LIMIT)).thenReturn(notes);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected notifications", 1,
        controller.findByCreatedAfter(0, MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedAfterLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findByCreatedAfter(0, MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindByCreatedAfterServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findByCreatedAfter(0, MAX_LIMIT))
        .thenThrow(new ServiceException(new RuntimeException("test")));
    controller.findByCreatedAfter(0, MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindByCreatedAfterException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findByCreatedAfter(0, MAX_LIMIT)).thenThrow(new RuntimeException("test"));
    System.out.println("in htere");
    controller.findByCreatedAfter(0, MAX_LIMIT);
  }

  @Test
  public void testFindByCreatedBefore() {
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    Mockito.when(handler.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT)).thenReturn(notes);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected notifications", 1,
        controller.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedBeforeLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindByCreatedBeforeServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindByCreatedBeforeException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT))
        .thenThrow(new RuntimeException("test"));
    controller.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT);
  }

  @Test
  public void testFindInLabels() {
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    Mockito.when(handler.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT)).thenReturn(notes);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected notifications", 1,
        controller.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindInLabelsLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindInLabelsServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindInLabelsException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT))
        .thenThrow(new RuntimeException("test"));
    controller.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT);
  }

  @Test
  public void testFindNewNotifications() {
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    Mockito.when(handler.findNewNotifications(MAX_LIMIT)).thenReturn(notes);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected notifications", 1,
        controller.findNewNotifications(MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindNewNotificationsLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findNewNotifications(MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindNewNotificationsServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findNewNotifications(MAX_LIMIT))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findNewNotifications(MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindNewNotificationsException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findNewNotifications(MAX_LIMIT)).thenThrow(new RuntimeException("test"));
    controller.findNewNotifications(MAX_LIMIT);
  }

  @Test
  public void testDeleteBySlug() {
    assertTrue("Delete by method did not return expected result",
        controller.deleteBySlug(NotificationData.TEST_SLUG));
  }

  @Test
  public void testDeleteOld() {
    assertTrue("Delete by method did not return expected result", controller.deleteOld(100));
  }

}
