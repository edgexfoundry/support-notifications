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
 * @microservice: support-logging
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
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.notifications.test.data.SubscriptionData;
import org.edgexfoundry.support.notifications.controller.impl.SubscriptionControllerImpl;
import org.edgexfoundry.support.notifications.service.SubscriptionHandler;
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
public class SubscriptionControllerTest {

  @InjectMocks
  private SubscriptionControllerImpl controller;

  @Mock
  private SubscriptionHandler handler;

  private Subscription sub;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    sub = SubscriptionData.newTestInstance();
  }

  @Test
  public void testCreateSubscription() {
    assertEquals("Create of new subscription did not return healthy status", HttpStatus.CREATED,
        controller.createSubscription(sub).getStatusCode());
  }

  @Test(expected = ServiceException.class)
  public void testCreateSubscriptionServiceException() {
    controller = new SubscriptionControllerImpl();
    controller.createSubscription(sub);
  }

  @Test(expected = ClientException.class)
  public void testCreateSubscriptoinBadSlug() {
    sub.setSlug(null);
    controller.createSubscription(sub);
  }

  @Test(expected = DataValidationException.class)
  public void testCreateSubscriptionDuplicateSlug() {
    Mockito.when(handler.findBySlug(SubscriptionData.TEST_SLUG)).thenReturn(sub);
    controller.createSubscription(sub);
  }

  @Test
  public void testUpdateSubscription() {
    assertTrue("Update of subscription did not work", controller.updateSubscription(sub));
  }

  @Test(expected = ServiceException.class)
  public void testUpdateSubscriptionServiceException() {
    controller = new SubscriptionControllerImpl();
    controller.updateSubscription(sub);
  }

  @Test
  public void testListAll() {
    List<Subscription> subs = new ArrayList<>();
    subs.add(sub);
    Mockito.when(handler.listAll()).thenReturn(subs);
    assertEquals("List all did not return the correct collection of Subs", 1,
        controller.listAll().size());
  }

  @Test(expected = ServiceException.class)
  public void testListAllServiceException() {
    controller = new SubscriptionControllerImpl();
    controller.listAll();
  }

  @Test(expected = NotFoundException.class)
  public void testListAllNotFoundException() {
    Mockito.when(handler.listAll())
        .thenThrow(new NotFoundException("Subscription", SubscriptionData.TEST_SLUG));
    controller.listAll();
  }

  @Test
  public void testFindBySlug() {
    Mockito.when(handler.findBySlug(SubscriptionData.TEST_SLUG)).thenReturn(sub);
    assertEquals("Find by method did not return expected subscription", sub,
        controller.findBySlug(SubscriptionData.TEST_SLUG));
  }

  @Test(expected = NotFoundException.class)
  public void testFindBySlugNotFound() {
    controller.findBySlug("foo");
  }

  @Test(expected = ServiceException.class)
  public void testFindBySlugServiceException() {
    Mockito.when(handler.findBySlug(SubscriptionData.TEST_SLUG))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findBySlug(SubscriptionData.TEST_SLUG);
  }

  @Test(expected = ServiceException.class)
  public void testFindBySlugException() {
    controller = new SubscriptionControllerImpl();
    controller.findBySlug(SubscriptionData.TEST_SLUG);
  }

  @Test
  public void testFindInSubscribedCategoriesOrSubscribedLabels() {
    List<Subscription> subs = new ArrayList<>();
    subs.add(sub);
    Mockito.when(handler.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
        SubscriptionData.TEST_LABELS)).thenReturn(subs);
    assertEquals("Find by did not return the correct collection of Subs", 1,
        controller.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
            SubscriptionData.TEST_LABELS).size());
  }

  @Test(expected = ServiceException.class)
  public void testFindInSubscribedCategoriesOrSubscribedLabelsServiceException() {
    controller = new SubscriptionControllerImpl();
    controller.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
        SubscriptionData.TEST_LABELS);
  }

  @Test(expected = NotFoundException.class)
  public void testFindInSubscribedCategoriesOrSubscribedLabelsNotFoundException() {
    Mockito
        .when(handler.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
            SubscriptionData.TEST_LABELS))
        .thenThrow(new NotFoundException("Subscription", SubscriptionData.TEST_SLUG));
    controller.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
        SubscriptionData.TEST_LABELS);
  }

  @Test
  public void testFindInSubscribedCategories() {
    List<Subscription> subs = new ArrayList<>();
    subs.add(sub);
    Mockito.when(handler.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
        new String[] {})).thenReturn(subs);
    assertEquals("Find by did not return the correct collection of Subs", 1,
        controller.findInSubscribedCategories(SubscriptionData.TEST_CAT_STR).size());
  }

  @Test(expected = ServiceException.class)
  public void testFindInSubscribedCategoriesServiceException() {
    controller = new SubscriptionControllerImpl();
    controller.findInSubscribedCategories(SubscriptionData.TEST_CAT_STR);
  }

  @Test(expected = NotFoundException.class)
  public void testFindInSubscribedCategoriesNotFoundException() {
    Mockito
        .when(handler.findInSubscribedCategoriesOrSubscribedLabels(SubscriptionData.TEST_CAT_STR,
            new String[] {}))
        .thenThrow(new NotFoundException("Subscription", SubscriptionData.TEST_SLUG));
    controller.findInSubscribedCategories(SubscriptionData.TEST_CAT_STR);
  }


  @Test
  public void testFindInSubscribedLabels() {
    List<Subscription> subs = new ArrayList<>();
    subs.add(sub);
    Mockito.when(handler.findInSubscribedCategoriesOrSubscribedLabels(new String[] {},
        SubscriptionData.TEST_LABELS)).thenReturn(subs);
    assertEquals("Find by did not return the correct collection of Subs", 1,
        controller.findInSubscribedLabels(SubscriptionData.TEST_LABELS).size());
  }

  @Test(expected = ServiceException.class)
  public void testFindInSubscribedLabelsServiceException() {
    controller = new SubscriptionControllerImpl();
    controller.findInSubscribedLabels(SubscriptionData.TEST_LABELS);
  }

  @Test(expected = NotFoundException.class)
  public void testFindInSubscribedLabelsNotFoundException() {
    Mockito
        .when(handler.findInSubscribedCategoriesOrSubscribedLabels(new String[] {},
            SubscriptionData.TEST_LABELS))
        .thenThrow(new NotFoundException("Subscription", SubscriptionData.TEST_SLUG));
    controller.findInSubscribedLabels(SubscriptionData.TEST_LABELS);
  }

  @Test
  public void testSearchByReceiver() {
    List<Subscription> subs = new ArrayList<>();
    subs.add(sub);
    Mockito.when(handler.searchByReceiver(SubscriptionData.TEST_REC)).thenReturn(subs);
    assertEquals("Search by did not return the correct collection of Subs", 1,
        controller.searchByReceiver(SubscriptionData.TEST_REC).size());
  }

  @Test(expected = ServiceException.class)
  public void testSearchByReceiverServiceException() {
    Mockito.when(handler.searchByReceiver(SubscriptionData.TEST_REC))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.searchByReceiver(SubscriptionData.TEST_REC);
  }

  @Test(expected = ServiceException.class)
  public void testSearchByReceiverException() {
    controller = new SubscriptionControllerImpl();
    controller.searchByReceiver(SubscriptionData.TEST_REC);
  }

  @Test
  public void testDeleteBySlug() {
    assertTrue("Delete by method did not return expected result",
        controller.deleteBySlug(SubscriptionData.TEST_SLUG));
  }
}
