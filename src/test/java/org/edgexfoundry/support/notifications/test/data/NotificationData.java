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

package org.edgexfoundry.support.notifications.test.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationCategory;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.support.domain.notifications.NotificationStatus;

public interface NotificationData {

  static final String TEST_SLUG = "test slug";
  static final String TEST_SENDER = "test sender";
  static final NotificationCategory TEST_CAT = NotificationCategory.SW_HEALTH;
  static final NotificationSeverity TEST_SEV = NotificationSeverity.NORMAL;
  static final String TEST_CONTENT = "test content";
  static final String TEST_DESC = "test description";
  static final String[] TEST_LABELS = {"label1", "label2"};
  static final NotificationStatus TEST_STATUS = NotificationStatus.NEW;
  static final Long TEST_CREATION = 1000L;


  static Notification newTestInstance() {
    Notification note = new Notification();
    note = new Notification();
    note.setSlug(TEST_SLUG);
    note.setSender(TEST_SENDER);
    note.setCategory(TEST_CAT);
    note.setSeverity(TEST_SEV);
    note.setContent(TEST_CONTENT);
    note.setDescription(TEST_DESC);
    note.setLabels(TEST_LABELS);
    note.setCreated(TEST_CREATION);
    note.setStatus(TEST_STATUS);
    return note;
  }

  static void checkTestData(Notification n, String id) {
    assertEquals("Notification id does not match expected", id, n.getId());
    assertEquals("Notification slug does not match expected", TEST_SLUG, n.getSlug());
    assertEquals("Notification sender does not match expected", TEST_SENDER, n.getSender());
    assertEquals("Notification category does not match expected", TEST_CAT, n.getCategory());
    assertEquals("Notification severity does not match expected", TEST_SEV, n.getSeverity());
    assertEquals("Notification content does not match expected", TEST_CONTENT, n.getContent());
    assertEquals("Notification description does not match expected", TEST_DESC, n.getDescription());
    assertEquals("Notification status does not match expected", TEST_STATUS, n.getStatus());
    assertArrayEquals("Notification labels do not match expected", TEST_LABELS, n.getLabels());
    assertNotNull("Notification modified date is null", n.getModified());
    assertNotNull("Notification created date is null", n.getCreated());
  }

}
