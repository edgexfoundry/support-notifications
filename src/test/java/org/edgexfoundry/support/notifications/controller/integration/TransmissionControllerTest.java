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

import java.util.List;

import org.edgexfoundry.EdgeXSupportNotificationsApplication;
import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.test.data.NotificationData;
import org.edgexfoundry.support.notifications.test.data.TransmissionData;
import org.edgexfoundry.support.notifications.controller.TransmissionController;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
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
public class TransmissionControllerTest {

  private static final int MAX_LIMIT = 10;

  @Autowired
  TransmissionController controller;

  @Autowired
  private TransmissionDAO dao;
  private String tId;

  @Autowired
  private NotificationDAO daoN;
  private String nId;
  private Notification note;

  @Before
  public void createTestData() {
    note = NotificationData.newTestInstance();
    daoN.save(note);
    nId = note.getId();
    Transmission trans = TransmissionData.newTestInstanceWONotification();
    trans.setNotification(note);
    dao.save(trans);
    tId = trans.getId();
  }

  /**
   * clean up data after tests. The delete operation also tests the delete operation.
   */
  @After
  public void destroyTestData() {
    dao.delete(tId);
    assertTrue("deleted test data still exists in the database", dao.findAll().isEmpty());
    daoN.delete(nId);
    assertTrue("deleted test data still exists in the database", daoN.findAll().isEmpty());
  }

  @Test
  public void testFindByNotificationSlug() {
    List<Transmission> transmissions =
        controller.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 1, transmissions.size());
    TransmissionData.checkTestData(transmissions.get(0), transmissions.get(0).getId());
  }

  @Test(expected = NotFoundException.class)
  public void testFindByNotificationSlugNoneFound() {
    controller.findByNotificationSlug("foo", MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByNotificationSlugLimitExceededException() {
    controller.findByNotificationSlug(NotificationData.TEST_SLUG, 1000);
  }


  @Test
  public void testFindByCreatedDuration() {
    List<Transmission> transmissions =
        controller.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 1, transmissions.size());
    TransmissionData.checkTestData(transmissions.get(0), transmissions.get(0).getId());
  }

  @Test
  public void testFindByCreatedDurationNoneFound() {
    List<Transmission> transmissions = controller.findByCreatedDuration(0, 1, MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 0, transmissions.size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedDurationLimitExceededException() {
    controller.findByCreatedDuration(0, Long.MAX_VALUE, 1000);
  }

  @Test
  public void testFindByCreatedAfter() {
    List<Transmission> trxs = controller.findByCreatedAfter(0, MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 1, trxs.size());
    TransmissionData.checkTestData(trxs.get(0), trxs.get(0).getId());
  }

  @Test
  public void testFindByCreatedAfterNoneFound() {
    List<Transmission> trxs = controller.findByCreatedAfter(Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 0, trxs.size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedAfterLimitExceededException() {
    controller.findByCreatedAfter(0, 1000);
  }

  @Test
  public void testFindByCreatedBefore() {
    List<Transmission> trxs = controller.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 1, trxs.size());
    TransmissionData.checkTestData(trxs.get(0), trxs.get(0).getId());
  }

  @Test
  public void testFindByCreatedBeforeNoneFound() {
    List<Transmission> trxs = controller.findByCreatedBefore(0, MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 0, trxs.size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByCreatedBeforeLimitExceededException() {
    controller.findByCreatedBefore(Long.MAX_VALUE, 1000);
  }

  @Test
  public void testFindEscalatedTransmissions() {
    List<Transmission> trxs = controller.findEscalatedTransmissions(MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 0, trxs.size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindEscalatedTransmissionsLimitExceededException() {
    controller.findEscalatedTransmissions(1000);
  }

  @Test
  public void testFindFailedTransmissions() {
    List<Transmission> trxs = controller.findFailedTransmissions(MAX_LIMIT);
    assertEquals("Find by method did not return expected transmissions", 0, trxs.size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindFailedTransmissionsLimitExceededException() {
    controller.findFailedTransmissions(1000);
  }

  @Test
  public void testDeleteOldSent() {
    assertTrue("Delete by method did not return expected result",
        controller.deleteOldSent(Long.MAX_VALUE));
  }

  @Test
  public void testDeleteOldEscalated() {
    assertTrue("Delete by method did not return expected result",
        controller.deleteOldEscalated(Long.MAX_VALUE));
  }

  @Test
  public void testDeleteOldAcknowledged() {
    assertTrue("Delete by method did not return expected result",
        controller.deleteOldAcknowledged(Long.MAX_VALUE));
  }

  @Test
  public void testDeleteOldFailed() {
    assertTrue("Delete by method did not return expected result",
        controller.deleteOldFailed(Long.MAX_VALUE));
  }

}
