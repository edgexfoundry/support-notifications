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

import java.util.List;

import org.edgexfoundry.EdgeXSupportNotificationsApplication;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.notification.test.data.SubscriptionData;
import org.edgexfoundry.support.notifications.dao.SubscriptionDAO;
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
public class SubscriptionDAOTest {

  @Autowired
  private SubscriptionDAO dao;
  private String subId;

  /**
   * Create and save an instance of the Notification before each test Note: the before method tests
   * the save operation of the Repository
   */
  @Before
  public void createTestData() {
    Subscription sub = SubscriptionData.newTestInstance();
    dao.save(sub);
    subId = sub.getId();
  }

  /**
   * clean up data after tests. The delete operation also tests the delete operation.
   */
  @After
  public void destroyTestData() {
    dao.delete(subId);
    assertTrue("deleted test data still exists in the database", dao.findAll().isEmpty());
  }

  @Test
  public void testfindBySlugIgnoreCase() {
    SubscriptionData.checkTestData(dao.findBySlugIgnoreCase(SubscriptionData.TEST_SLUG), subId);
  }

  @Test
  public void testfindByReceiverLikeIgnoreCase() {
    List<Subscription> subs = dao.findByReceiverLikeIgnoreCase(SubscriptionData.TEST_REC);
    assertEquals("Search returned more than expected number of subscriptions", 1, subs.size());
    SubscriptionData.checkTestData(subs.get(0), subId);
  }


}
