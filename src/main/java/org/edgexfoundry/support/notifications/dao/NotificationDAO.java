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
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.notifications.dao;

import java.util.List;

import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.support.domain.notifications.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationDAO extends MongoRepository<Notification, String> {

  public Notification findBySlugIgnoreCase(String slug);

  public List<Notification> findBySenderLikeIgnoreCase(String sender);

  public Page<Notification> findBySenderLikeIgnoreCase(String sender, Pageable pageable);

  public List<Notification> findByCreatedBetween(long start, long end);

  public Page<Notification> findByCreatedBetween(long start, long end, Pageable pageable);

  public List<Notification> findByCreatedAfter(long start);

  public Page<Notification> findByCreatedAfter(long start, Pageable pageable);

  public List<Notification> findByCreatedBefore(long end);

  public Page<Notification> findByCreatedBefore(long end, Pageable pageable);

  // @Query("{ 'labels': { '$in': ?0 } }")
  public List<Notification> findByLabelsInIgnoreCase(String[] labels);

  // @Query("{ 'labels': { '$in': ?0 } }")
  public Page<Notification> findByLabelsInIgnoreCase(String[] labels, Pageable pageable);

  public List<Notification> findByStatus(NotificationStatus status);

  public Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

  public List<Notification> findBySeverityAndStatus(NotificationSeverity severity,
      NotificationStatus status);

  public Page<Notification> findBySeverityAndStatus(NotificationSeverity severity,
      NotificationStatus status, Pageable pageable);

  public List<Notification> findByStatusAndModifiedBefore(NotificationStatus status, long end);

  public List<Notification> findByModifiedBefore(long end);

}
