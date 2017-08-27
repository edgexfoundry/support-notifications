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
import org.edgexfoundry.support.notifications.config.MailChannelProperties;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.EscalationService;
import org.edgexfoundry.support.notifications.service.impl.EMAILSendingService;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Category(RequiresNone.class)
public class EMAILSendingServiceTest {

  private static final String TEST_RECV = "test@receiver.com";

  @InjectMocks
  private EMAILSendingService service;

  @Mock
  private MailChannelProperties mailChannelProps;

  @Mock
  private TransmissionDAO dao;

  @Mock
  private EscalationService escalation;

  @Mock
  private ThreadPoolTaskScheduler scheduler;

  @Mock
  private GeneralConfig generalConfig;

  @Mock
  private JavaMailSender mailSendor;

  private Notification note;

  private EmailChannel channel;


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    note = new Notification();
    channel = new EmailChannel();
    channel.setMailAddresses(new String[] {"foo@bar.com"});
    channel.setType(ChannelType.EMAIL);
  }

  @Test
  public void testSend() {
    Transmission t = new Transmission();
    Mockito.when(dao.insert(Mockito.any(Transmission.class))).thenReturn(t);
    service.send(note, channel, TEST_RECV);
  }

  @Test(expected = ServiceException.class)
  public void testSendException() {
    Mockito.when(dao.insert(Mockito.any(Transmission.class)))
        .thenThrow(new RuntimeException("test"));
    service.send(note, channel, TEST_RECV);
  }

  @Test(expected = DataValidationException.class)
  public void testSendNotEmailChannel() {
    RESTfulChannel rChannel = new RESTfulChannel();
    service.send(note, rChannel, TEST_RECV);
  }

  @Test(expected = DataValidationException.class)
  public void testSendNoAddress() {
    channel.setMailAddresses(null);
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
