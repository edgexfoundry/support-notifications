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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.NotificationCategory;
import org.edgexfoundry.support.domain.notifications.Subscription;

public interface SubscriptionData {

  static final String TEST_DESC = "test description";
  static final String TEST_REC = "test receiver";
  static final String TEST_SLUG = "test slug";
  static final Channel[] TEST_CHANNELS = {};
  static final String[] TEST_LABELS = {"label1", "label2"};
  static final String[] TEST_CAT_STR = {"HW_Health", "SW_HEALTH"};
  static final NotificationCategory[] TEST_CATS =
      {NotificationCategory.HW_HEALTH, NotificationCategory.SW_HEALTH};


  static Subscription newTestInstance() {
    Subscription sub = new Subscription();
    sub = new Subscription();
    sub.setChannels(TEST_CHANNELS);
    sub.setCreated(1000);
    sub.setDescription(TEST_DESC);
    sub.setReceiver(TEST_REC);
    sub.setSlug(TEST_SLUG);
    sub.setSubscribedCategories(TEST_CATS);
    sub.setSubscribedLabels(TEST_LABELS);
    return sub;
  }

  static void checkTestData(Subscription s, String id) {
    assertEquals("Subscription id does not match expected", id, s.getId());
    assertEquals("Subscription slug does not match expected", TEST_SLUG, s.getSlug());
    assertEquals("Subscription receiver does not match expected", TEST_REC, s.getReceiver());
    assertEquals("Subscription description does not match expected", TEST_DESC, s.getDescription());
    assertArrayEquals("Subscription channels does not match expected", TEST_CHANNELS,
        s.getChannels());
    assertArrayEquals("Subscription labels does not match expected", TEST_LABELS,
        s.getSubscribedLabels());
    assertNotNull("Subscription modified date is null", s.getModified());
    assertNotNull("Subscription created date is null", s.getCreated());
  }

}
