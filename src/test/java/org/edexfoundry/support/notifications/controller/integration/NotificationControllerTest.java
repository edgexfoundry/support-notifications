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

package org.edexfoundry.support.notifications.controller.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.edgexfoundry.EdgeXSupportNotificationsApplication;
import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notification.test.data.NotificationData;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.controller.impl.NotificationControllerImpl;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSpring;
import org.edgexfoundry.test.category.RequiresWeb;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EdgeXSupportNotificationsApplication.class)
@WebAppConfiguration("src/test/resources")
@Category({RequiresMongoDB.class, RequiresSpring.class, RequiresWeb.class})
public class NotificationControllerTest {

  private static final int MAX_LIMIT = 10;

  @Autowired
  private NotificationControllerImpl controller;

  @Autowired
  private GeneralConfig generalConfig;
  
  private int max;

  /**
   * setup also tests receiverNotification
   */
  @Before
  public void setup() {
    max = generalConfig.getReadLimit()+1;
    controller.receiveNotification(NotificationData.newTestInstance());
  }

  /**
   * cleanup also tests delete by slug
   */
  @After
  public void cleanup() {
    controller.deleteBySlug(NotificationData.TEST_SLUG);
  }


  @Test(expected = ClientException.class)
  public void testReceiveNotificationBadSlug() {
    Notification note2 = NotificationData.newTestInstance();
    note2.setSlug(null);
    controller.receiveNotification(note2);
  }

  @Test(expected = DataValidationException.class)
  public void testReceiveNotificationDuplicateSlug() {
    Notification note = NotificationData.newTestInstance();
    controller.receiveNotification(note);
  }

  @Test
  public void testFindBySlug() {
    Notification note = controller.findBySlug(NotificationData.TEST_SLUG);
    NotificationData.checkTestData(note, note.getId());
  }

  @Test(expected = NotFoundException.class)
  public void testFindBySlugNotFound() {
    controller.findBySlug("foo");
  }

  @Test
  public void testFindBySender() {
    List<Notification> notes = controller.findBySender(NotificationData.TEST_SENDER, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 1, notes.size());
    NotificationData.checkTestData(notes.get(0), notes.get(0).getId());
  }

  @Test
  public void testFindBySenderNoneFound() {
    List<Notification> notes = controller.findBySender("foo", MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 0, notes.size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindBySenderLimitExceededException() {
    controller.findBySender(NotificationData.TEST_SENDER, Integer.MAX_VALUE);
  }

  @Test
  public void testFindByCreatedDuration() {
    List<Notification> notes = controller.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 1, notes.size());
    NotificationData.checkTestData(notes.get(0), notes.get(0).getId());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedDurationLimitExceededException() {
    controller.findByCreatedDuration(0, Long.MAX_VALUE, max);
  }

  @Test
  public void testFindByCreatedDurationNoneFound() {
    List<Notification> notes = controller.findByCreatedDuration(0, 1, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 0, notes.size());
  }

  @Test
  public void testFindByCreatedAfter() {
    List<Notification> notes = controller.findByCreatedAfter(0, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 1, notes.size());
    NotificationData.checkTestData(notes.get(0), notes.get(0).getId());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedAfterLimitExceededException() {
    controller.findByCreatedAfter(0, max);
  }

  @Test
  public void testFindByCreatedAfterNoneFound() {
    List<Notification> notes = controller.findByCreatedAfter(Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 0, notes.size());
  }

  @Test
  public void testFindByCreatedBefore() {
    List<Notification> notes = controller.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 1, notes.size());
    NotificationData.checkTestData(notes.get(0), notes.get(0).getId());
  }


  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedBeforeLimitExceededException() {
    controller.findByCreatedAfter(Long.MAX_VALUE, max);
  }

  @Test
  public void testFindByCreatedBeforeNoneFound() {
    List<Notification> notes = controller.findByCreatedBefore(0, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 0, notes.size());
  }

  @Test
  public void testFindInLabels() {
    List<Notification> notes = controller.findInLabels(NotificationData.TEST_LABELS, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 1, notes.size());
    NotificationData.checkTestData(notes.get(0), notes.get(0).getId());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindInLabelsLimitExceededException() {
    controller.findInLabels(NotificationData.TEST_LABELS, max);
  }

  @Test
  public void testFindInLabelsNoneFound() {
    List<Notification> notes = controller.findInLabels(new String[] {"foo", "bar"}, MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 0, notes.size());
  }

  @Test
  public void testFindNewNotifications() {
    List<Notification> notes = controller.findNewNotifications(MAX_LIMIT);
    assertEquals("Find by method did not return expected notifications", 1, notes.size());
    NotificationData.checkTestData(notes.get(0), notes.get(0).getId());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindNewNotificationsLimitExceededException() {
    controller.findNewNotifications(max);
  }


  @Test
  public void testDeleteOld() {
    assertTrue("Delete by method did not return expected result", controller.deleteOld(100));
  }

}
