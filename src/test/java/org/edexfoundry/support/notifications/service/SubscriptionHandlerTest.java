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
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.notification.test.data.SubscriptionData;
import org.edgexfoundry.support.notifications.dao.SubscriptionDAO;
import org.edgexfoundry.support.notifications.service.impl.SubscriptionHandlerImpl;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;

import com.mongodb.MongoException;

@Category(RequiresNone.class)
public class SubscriptionHandlerTest {


  @InjectMocks
  private SubscriptionHandlerImpl handler;

  private Subscription sub;

  @Mock
  private SubscriptionDAO subscriptionDAO;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    sub = SubscriptionData.newTestInstance();
  }

  @Test
  public void testCreateSubscription() {
    Mockito.when(subscriptionDAO.insert(sub)).thenReturn(sub);
    handler.createSubscription(sub);
  }

  @Test(expected = ClientException.class)
  public void testCreateSubscriptionNull() {
    handler.createSubscription(null);
  }

  @Test(expected = DataValidationException.class)
  public void testCreateSubscriptionDuplKey() {
    Mockito.when(subscriptionDAO.insert(sub)).thenThrow(new DuplicateKeyException("test"));
    handler.createSubscription(sub);
  }


  @Test
  public void testFindInSubscribedCategoriesOrSubscribedLabels() {
    assertTrue("Should not return any Subscriptions",
        handler.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
            SubscriptionData.TEST_LABELS).isEmpty());
  }

  @Test
  public void testFindBySlug() {
    assertNull("Did not return null on find by", handler.findBySlug(SubscriptionData.TEST_SLUG));
  }

  @Test
  public void testSearchByReceiver() {
    assertTrue("Should not return any Subscriptions",
        handler.searchByReceiver(SubscriptionData.TEST_REC).isEmpty());
  }

  @Test
  public void testListAll() {
    assertTrue("Should not return any Subscriptions", handler.listAll().isEmpty());
  }

  @Test(expected = NotFoundException.class)
  public void testUpdateSubscription() {
    handler.updateSubscription(sub);
  }

  @Test(expected = ClientException.class)
  public void testUpdateSubscriptionWithNull() {
    handler.updateSubscription(null);
  }

  @Test
  public void testUpdateSubscriptionWithOldSubscription() {
    Mockito.when(subscriptionDAO.findBySlugIgnoreCase(sub.getSlug())).thenReturn(sub);
    Mockito.when(subscriptionDAO.save(sub)).thenReturn(sub);
    handler.updateSubscription(sub);
  }

  @Test(expected = ClientException.class)
  public void testUpdateSubscriptionMongoException() {
    Mockito.when(subscriptionDAO.findBySlugIgnoreCase(sub.getSlug())).thenReturn(sub);
    Mockito.when(subscriptionDAO.save(sub)).thenThrow(new MongoException(1, "test"));
    handler.updateSubscription(sub);
  }

  @Test
  public void testDeleteBySlug() {
    Mockito.when(subscriptionDAO.findBySlugIgnoreCase(sub.getSlug())).thenReturn(sub);
    handler.deleteBySlug(SubscriptionData.TEST_SLUG);
  }

  @Test(expected = ClientException.class)
  public void testDeleteBySlugWithNull() {
    handler.deleteBySlug(null);
  }

  @Test(expected = NotFoundException.class)
  public void testDeleteBySlugNotFound() {
    handler.deleteBySlug(SubscriptionData.TEST_SLUG);
  }

}
