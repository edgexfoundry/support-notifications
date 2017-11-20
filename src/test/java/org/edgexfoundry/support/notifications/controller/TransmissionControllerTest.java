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

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.test.data.NotificationData;
import org.edgexfoundry.support.notifications.test.data.TransmissionData;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.controller.impl.TransmissionControllerImpl;
import org.edgexfoundry.support.notifications.service.TransmissionHandler;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@Category(RequiresNone.class)
public class TransmissionControllerTest {

  private static final int MAX_LIMIT = 10;

  @InjectMocks
  private TransmissionControllerImpl controller;

  @Mock
  private TransmissionHandler handler;

  @Mock
  private GeneralConfig config;

  private Transmission trans;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    trans = TransmissionData.newTestInstance();
  }

  @Test
  public void testFindByNotificationSlug() {
    List<Transmission> transmissions = new ArrayList<>();
    transmissions.add(trans);
    Mockito.when(handler.findByNotificationSlug(NotificationData.TEST_SLUG, Integer.MAX_VALUE))
        .thenReturn(transmissions);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    controller.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT);
  }

  @Test(expected = LimitExceededException.class)
  public void testFindByNotificationSlugLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindByNotificationSlugServiceException() {
    Mockito.when(handler.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT))
        .thenThrow(new RuntimeException());
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    controller.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT);
  }

  @Test(expected = DataValidationException.class)
  public void testFindByNotificationSlugException() {
    Mockito.when(handler.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT))
        .thenThrow(new DataValidationException(NotificationData.TEST_SLUG));
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    controller.findByNotificationSlug(NotificationData.TEST_SLUG, MAX_LIMIT);
  }

  @Test
  public void testFindByCreatedDuration() {
    List<Transmission> trxs = new ArrayList<>();
    trxs.add(trans);
    Mockito.when(handler.findByCreatedDuration(0, Long.MAX_VALUE, MAX_LIMIT)).thenReturn(trxs);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected transmissions", 1,
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
    List<Transmission> trxs = new ArrayList<>();
    trxs.add(trans);
    Mockito.when(handler.findByCreatedAfter(0, MAX_LIMIT)).thenReturn(trxs);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected tansmissions", 1,
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
    List<Transmission> trxs = new ArrayList<>();
    trxs.add(trans);
    Mockito.when(handler.findByCreatedBefore(Long.MAX_VALUE, MAX_LIMIT)).thenReturn(trxs);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected transmissions", 1,
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
  public void testFindEscalatedTransmissions() {
    List<Transmission> trxs = new ArrayList<>();
    trxs.add(trans);
    Mockito.when(handler.findEscalatedTransmissions(MAX_LIMIT)).thenReturn(trxs);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected transmissions", 1,
        controller.findEscalatedTransmissions(MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindEscalatedTransmissionsLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findEscalatedTransmissions(MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindEscalatedTransmissionsServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findEscalatedTransmissions(MAX_LIMIT))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findEscalatedTransmissions(MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindEscalatedTransmissionsException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findEscalatedTransmissions(MAX_LIMIT))
        .thenThrow(new RuntimeException("test"));
    controller.findEscalatedTransmissions(MAX_LIMIT);
  }

  @Test
  public void testFindFailedTransmissions() {
    List<Transmission> trxs = new ArrayList<>();
    trxs.add(trans);
    Mockito.when(handler.findFailedTransmissions(MAX_LIMIT)).thenReturn(trxs);
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    assertEquals("Find by method did not return expected transmissions", 1,
        controller.findFailedTransmissions(MAX_LIMIT).size());
  }

  @Test(expected = LimitExceededException.class)
  public void testFindFailedTransmissionsLimitExceededException() {
    Mockito.when(config.getReadLimit()).thenReturn(0);
    controller.findFailedTransmissions(MAX_LIMIT);
  }

  @Test(expected = ServiceException.class)
  public void testFindFailedTransmissionsServiceException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findFailedTransmissions(MAX_LIMIT))
        .thenThrow(new ServiceException(new Exception("test")));
    controller.findFailedTransmissions(MAX_LIMIT);
  }

  @Test(expected = Exception.class)
  public void testFindFailedTransmissionsException() {
    Mockito.when(config.getReadLimit()).thenReturn(Integer.MAX_VALUE);
    Mockito.when(handler.findFailedTransmissions(MAX_LIMIT))
        .thenThrow(new RuntimeException("test"));
    controller.findFailedTransmissions(MAX_LIMIT);
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
