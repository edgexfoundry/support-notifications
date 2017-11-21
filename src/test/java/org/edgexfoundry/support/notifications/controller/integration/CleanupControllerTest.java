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

package org.edgexfoundry.support.notifications.controller.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.edgexfoundry.EdgeXSupportNotificationsApplication;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notifications.test.data.NotificationData;
import org.edgexfoundry.support.notifications.controller.CleanupController;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EdgeXSupportNotificationsApplication.class)
@WebAppConfiguration("src/test/resources")
@Category({RequiresMongoDB.class, RequiresSpring.class, RequiresWeb.class})
public class CleanupControllerTest {

  @Autowired
  CleanupController controller;

  @Autowired
  private NotificationDAO dao;
  private String noteId;

  @Before
  public void setup() {
    Notification note = NotificationData.newTestInstance();
    dao.save(note);
    noteId = note.getId();
  }

  @After
  public void cleanup() {
    dao.delete(noteId);
    assertTrue("deleted test data still exists in the database", dao.findAll().isEmpty());
  }

  @Test
  public void testCleanupOld() throws InterruptedException {
    assertEquals("One notification should exist in the database", 1, dao.findAll().size());
    ResponseEntity<Void> resp = controller.cleanupOld();
    assertEquals("Request to cleanup old requests did not return healthy status",
        HttpStatus.ACCEPTED, resp.getStatusCode());
    Thread.sleep(1000); // wait for database eventual consistency
    assertEquals("No notifications should have been cleaned up", 1, dao.findAll().size());
  }

  @Test
  public void testCleanup() throws InterruptedException {
    assertEquals("One notification should exist in the database", 1, dao.findAll().size());
    ResponseEntity<Void> resp = controller.cleanupOld(-10000); // clean up modified less than 10
                                                               // seconds from now
    assertEquals("Request to cleanup old requests did not return healthy status",
        HttpStatus.ACCEPTED, resp.getStatusCode());
    Thread.sleep(1000); // wait for database eventual consistency
    assertTrue("Notifications should have been all removed", dao.findAll().isEmpty());
  }

}
