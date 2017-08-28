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

package org.edgexfoundry.support.notifications.service;

import java.util.List;

import org.edgexfoundry.support.domain.notifications.Transmission;

public interface TransmissionHandler {

  public List<Transmission> findByCreatedDuration(long start, long end);

  public List<Transmission> findByCreatedAfter(long start);

  public List<Transmission> findByCreatedBefore(long end);

  public List<Transmission> findByNotificationSlug(String slug);

  public List<Transmission> findEscalatedTransmissions();

  public List<Transmission> findFailedTransmissions();

  public List<Transmission> findByCreatedDuration(long start, long end, int limit);

  public List<Transmission> findByCreatedAfter(long start, int limit);

  public List<Transmission> findByCreatedBefore(long end, int limit);

  public List<Transmission> findByNotificationSlug(String slug, int limit);

  public List<Transmission> findEscalatedTransmissions(int limit);

  public List<Transmission> findFailedTransmissions(int limit);

  public void deleteOldSentTransmissions(long age);

  public void deleteOldFailedTransmissions(long age);

  public void deleteOldEscalatedTransmissions(long age);

  public void deleteOldAcknowledgedTransmissions(long age);

}
