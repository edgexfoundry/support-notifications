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

package org.edgexfoundry.support.notifications.dao.integration.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.EdgeXSupportNotificationsApplication;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notification.test.data.NotificationData;
import org.edgexfoundry.support.notification.test.data.TransmissionData;
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
public class TransmissionDAOTest {

  @Autowired
  private TransmissionDAO dao;
  private String tId;

  @Autowired
  private NotificationDAO daoN;
  private String nId;
  private Notification note;

  /**
   * Create and save an instance of the Notification before each test Note: the before method tests
   * the save operation of the Repository
   */
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
  public void testfindByCreatedBetween() {
    List<Transmission> trans = dao.findByCreatedBetween(0, Long.MAX_VALUE);
    assertEquals("Search returned more than expected number of transmissions", 1, trans.size());
    TransmissionData.checkTestData(trans.get(0), tId);
  }

  @Test
  public void testfindByCreatedAfter() {
    List<Transmission> trans = dao.findByCreatedAfter(0);
    assertEquals("Search returned more than expected number of transmissions", 1, trans.size());
    TransmissionData.checkTestData(trans.get(0), tId);
  }

  @Test
  public void testfindByCreatedBefore() {
    List<Transmission> trans = dao.findByCreatedBefore(Long.MAX_VALUE);
    assertEquals("Search returned more than expected number of transmissions", 1, trans.size());
    TransmissionData.checkTestData(trans.get(0), tId);
  }

  @Test
  public void testfindByStatus() {
    List<Transmission> trans = dao.findByStatus(TransmissionData.TEST_STATUS);
    assertEquals("Search returned more than expected number of transmissions", 1, trans.size());
    TransmissionData.checkTestData(trans.get(0), tId);
  }

  @Test
  public void testfindByStatusAndResendCountLessThan() {
    List<Transmission> trans =
        dao.findByStatusAndResendCountLessThan(TransmissionData.TEST_STATUS, Integer.MAX_VALUE);
    assertEquals("Search returned more than expected number of transmissions", 1, trans.size());
    TransmissionData.checkTestData(trans.get(0), tId);
  }

  @Test
  public void testfindByNotificationId() {
    List<Transmission> trans = dao.findByNotificationId(nId);
    assertEquals("Search returned more than expected number of transmissions", 1, trans.size());
    TransmissionData.checkTestData(trans.get(0), tId);
  }

  @Test
  public void testdeleteByNotificationIn() {
    List<Notification> notes = new ArrayList<>();
    notes.add(note);
    dao.deleteByNotificationIn(notes);
    assertTrue("delete all by notifications did not work properly as the transmission still exists",
        dao.findAll().isEmpty());
  }

  @Test
  public void testdeleteByNotificationId() {
    dao.deleteByNotificationId(nId);
    assertTrue("delete all by notifications did not work properly as the transmission still exists",
        dao.findAll().isEmpty());
  }

  @Test
  public void testdeleteByStatusAndModifiedBefore() {
    dao.deleteByStatusAndModifiedBefore(TransmissionData.TEST_STATUS, Long.MAX_VALUE);
    assertTrue("delete did not work properly as the transmission still exists",
        dao.findAll().isEmpty());
  }

  @Test
  public void testdeleteByStatusAndResendCountGreaterThanEqualAndModifiedBefore() {
    dao.deleteByStatusAndResendCountGreaterThanEqualAndModifiedBefore(TransmissionData.TEST_STATUS,
        TransmissionData.TEST_RESEND_CNT, Long.MAX_VALUE);
    assertTrue("delete did not work properly as the transmission still exists",
        dao.findAll().isEmpty());
  }


}
