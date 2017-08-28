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

package org.edgexfoundry.support.notification.test.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;

public interface TransmissionData {

  static final String TEST_RECV = "test receiver";
  static final int TEST_RESEND_CNT = 2;
  static final TransmissionStatus TEST_STATUS = TransmissionStatus.SENT;

  static Transmission newTestInstance() {
    Transmission trans = new Transmission();
    trans.setCreated(1000);
    trans.setNotification(NotificationData.newTestInstance());
    trans.setReceiver(TEST_RECV);
    trans.setResendCount(TEST_RESEND_CNT);
    trans.setStatus(TEST_STATUS);
    return trans;
  }

  static Transmission newTestInstanceWONotification() {
    Transmission trans = new Transmission();
    trans.setCreated(1000);
    trans.setReceiver(TEST_RECV);
    trans.setResendCount(TEST_RESEND_CNT);
    trans.setStatus(TEST_STATUS);
    return trans;
  }

  static void checkTestData(Transmission t, String id) {
    assertEquals("Transmission id does not match expected", id, t.getId());
    assertEquals("Transmission receiver does not match expected", TEST_RECV, t.getReceiver());
    assertEquals("Transmission status does not match expected", TEST_STATUS, t.getStatus());
    assertEquals("Transmission resend count does not match expected", TEST_RESEND_CNT,
        t.getResendCount());
    assertNotNull("Transmission modified date is null", t.getModified());
    assertNotNull("Transmission created date is null", t.getCreated());
  }
}
