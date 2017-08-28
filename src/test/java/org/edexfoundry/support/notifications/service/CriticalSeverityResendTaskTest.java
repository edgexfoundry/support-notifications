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

import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.service.SendingService;
import org.edgexfoundry.support.notifications.service.impl.CriticalSeverityResendTask;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RequiresNone.class)
public class CriticalSeverityResendTaskTest {

  private CriticalSeverityResendTask task;

  private Transmission trans;
  private SendingService sending;

  @Before
  public void setup() {
    trans = new Transmission();
    sending = new MockSendingService();
  }

  @Test
  public void testRun() {
    task = new CriticalSeverityResendTask(trans, sending);
    task.run();
  }

  class MockSendingService implements SendingService {

    @Override
    public void send(Notification notification, Channel channel, String receiver) {}

    @Override
    public void resend(Transmission transmission) {}

  }

}

