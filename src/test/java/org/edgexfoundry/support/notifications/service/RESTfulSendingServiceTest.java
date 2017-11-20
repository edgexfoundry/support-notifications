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
package org.edgexfoundry.support.notifications.service;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.ChannelType;
import org.edgexfoundry.support.domain.notifications.EmailChannel;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.RESTfulChannel;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.domain.notifications.TransmissionRecord;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.EscalationService;
import org.edgexfoundry.support.notifications.service.impl.RESTfulSendingService;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.RequestMethod;

@Category(RequiresNone.class)
public class RESTfulSendingServiceTest {

  private static final String TEST_RECV = "testreceiver";

  @InjectMocks
  private RESTfulSendingService service;

  @Mock
  private TransmissionDAO dao;

  @Mock
  private EscalationService escalation;

  @Mock
  private ThreadPoolTaskScheduler scheduler;

  @Mock
  private GeneralConfig generalConfig;

  private Notification note;

  private RESTfulChannel channel;


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    note = new Notification();
    channel = new RESTfulChannel();
    channel.setType(ChannelType.REST);
    channel.setUrl("http://www.google.com");
    channel.setHttpMethod(RequestMethod.GET);
  }

  @Test
  public void testSend() {
    Transmission t = new Transmission();
    Mockito.when(dao.insert(Mockito.any(Transmission.class))).thenReturn(t);
    service.send(note, channel, TEST_RECV);
  }

  @Test
  public void testSendBadURLRestClientException() {
    Transmission t = new Transmission();
    channel.setUrl("http://localhost:9999");
    Mockito.when(dao.insert(Mockito.any(Transmission.class))).thenReturn(t);
    service.send(note, channel, TEST_RECV);
  }

  @Test(expected = DataValidationException.class)
  public void testSendNotRESTfulChannel() {
    EmailChannel eChannel = new EmailChannel();
    service.send(note, eChannel, TEST_RECV);
  }

  @Test(expected = DataValidationException.class)
  public void testSendNoUrl() {
    channel.setUrl(null);
    service.send(note, channel, TEST_RECV);
  }

   @Test
   public void testResend() {
   Transmission trans = new Transmission();
   trans.setChannel(channel);
   trans.setNotification(note);
   trans.setRecords(new TransmissionRecord[] {});
   trans.setStatus(TransmissionStatus.FAILED);
   Mockito.when(dao.save(Mockito.any(Transmission.class))).thenReturn(trans);
    service.resend(trans);
   }

  @Test(expected = ServiceException.class)
  public void testResendException() {
    Transmission trans = new Transmission();
    trans.setChannel(channel);
    trans.setNotification(note);
    trans.setRecords(new TransmissionRecord[] {});
    trans.setStatus(TransmissionStatus.FAILED);
    Mockito.when(dao.save(Mockito.any(Transmission.class))).thenThrow(new RuntimeException("test"));
    service.resend(trans);
  }

}
