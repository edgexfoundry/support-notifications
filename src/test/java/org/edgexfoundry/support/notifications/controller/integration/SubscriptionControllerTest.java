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
import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.notifications.test.data.SubscriptionData;
import org.edgexfoundry.support.notifications.controller.SubscriptionController;
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
public class SubscriptionControllerTest {

  @Autowired
  private SubscriptionController controller;

  /**
   * setup also tests createSubscription
   */
  @Before
  public void setup() {
    controller.createSubscription(SubscriptionData.newTestInstance());
  }

  /**
   * cleanup also tests delete by slug
   */
  @After
  public void cleanup() {
    controller.deleteBySlug(SubscriptionData.TEST_SLUG);
  }

  @Test(expected = ClientException.class)
  public void testCreateSubscriptoinBadSlug() {
    Subscription sub2 = SubscriptionData.newTestInstance();
    sub2.setSlug(null);
    controller.createSubscription(sub2);
  }

  @Test(expected = DataValidationException.class)
  public void testCreateSubscriptionDuplicateSlug() {
    Subscription sub2 = SubscriptionData.newTestInstance();
    controller.createSubscription(sub2);
  }

  @Test
  public void testUpdateSubscription() {
    Subscription sub2 = SubscriptionData.newTestInstance();
    assertTrue("Update of subscription did not work", controller.updateSubscription(sub2));
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateSubscriptionNotFoundExceptionException() {
    Subscription sub2 = SubscriptionData.newTestInstance();
    sub2.setSlug("foo");
    controller.updateSubscription(sub2);
  }

  @Test
  public void testListAll() {
    List<Subscription> subs = controller.listAll();
    assertEquals("Find by method did not return expected subscription", 1, subs.size());
    SubscriptionData.checkTestData(subs.get(0), subs.get(0).getId());
  }

  @Test
  public void testFindBySlug() {
    Subscription subx = controller.findBySlug(SubscriptionData.TEST_SLUG);
    SubscriptionData.checkTestData(subx, subx.getId());
  }

  @Test(expected = NotFoundException.class)
  public void testFindBySlugNotFound() {
    controller.findBySlug("foo");
  }

  @Test
  public void testFindInSubscribedCategoriesOrSubscribedLabels() {
    List<Subscription> subs = controller.findInSubscribedCategoriesOrSubscribedLabels(
        SubscriptionData.TEST_CAT_STR, SubscriptionData.TEST_LABELS);
    assertEquals("Find by method did not return expected subscription", 1, subs.size());
    SubscriptionData.checkTestData(subs.get(0), subs.get(0).getId());
  }

  @Test
  public void testFindInSubscribedCategoriesOrSubscribedLabelsNotFoundException() {
    assertEquals("Find by method did not return expected subscription", 0, controller
        .findInSubscribedCategoriesOrSubscribedLabels(new String[] {"foo"}, new String[] {"bar"})
        .size());
  }

  @Test
  public void testFindInSubscribedCategories() {
    List<Subscription> subs = controller.findInSubscribedCategories(SubscriptionData.TEST_CAT_STR);
    assertEquals("Find by method did not return expected subscription", 1, subs.size());
    SubscriptionData.checkTestData(subs.get(0), subs.get(0).getId());
  }

  @Test
  public void testFindInSubscribedCategoriesNotFoundException() {
    assertEquals("Find by method did not return expected subscription", 0,
        controller.findInSubscribedCategories(new String[] {"foo"}).size());
  }


  @Test
  public void testFindInSubscribedLabels() {
    List<Subscription> subs = controller.findInSubscribedLabels(SubscriptionData.TEST_LABELS);
    assertEquals("Find by method did not return expected subscription", 1, subs.size());
    SubscriptionData.checkTestData(subs.get(0), subs.get(0).getId());
  }

  @Test
  public void testFindInSubscribedLabelsNotFoundException() {
    assertEquals("Find by method did not return expected subscription", 0,
        controller.findInSubscribedLabels(new String[] {"foo"}).size());
  }

  @Test
  public void testSearchByReceiver() {
    List<Subscription> subs = controller.searchByReceiver(SubscriptionData.TEST_REC);
    assertEquals("Find by method did not return expected subscription", 1, subs.size());
    SubscriptionData.checkTestData(subs.get(0), subs.get(0).getId());
  }

  @Test
  public void testSearchByReceiverNotFound() {
    assertEquals("Find by method did not return expected subscription", 0,
        controller.searchByReceiver("foo").size());
  }

}
