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

import java.util.Collection;
import java.util.List;

import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransmissionDAO extends MongoRepository<Transmission, String> {

  public List<Transmission> findByCreatedBetween(long start, long end);

  public Page<Transmission> findByCreatedBetween(long start, long end, Pageable pageable);

  public List<Transmission> findByCreatedAfter(long start);

  public Page<Transmission> findByCreatedAfter(long start, Pageable pageable);

  public List<Transmission> findByCreatedBefore(long end);

  public Page<Transmission> findByCreatedBefore(long end, Pageable pageable);

  public List<Transmission> findByNotificationId(String notificationId);

  public Page<Transmission> findByNotificationId(String notificationId, Pageable pageable);

  public List<Transmission> findByStatus(TransmissionStatus status);

  public Page<Transmission> findByStatus(TransmissionStatus status, Pageable pageable);

  public List<Transmission> findByStatusAndResendCountLessThan(TransmissionStatus status,
      int resendLimit);

  public Page<Transmission> findByStatusAndResendCountLessThan(TransmissionStatus status,
      int resendLimit, Pageable pageable);

  public void deleteByNotificationIn(Collection<Notification> notifications);

  public void deleteByNotificationId(String notificationId);

  public void deleteByStatusAndModifiedBefore(TransmissionStatus status, long end);

  public void deleteByStatusAndResendCountGreaterThanEqualAndModifiedBefore(
      TransmissionStatus status, int resendCount, long end);

}
