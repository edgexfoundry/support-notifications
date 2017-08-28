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

package org.edgexfoundry.support.notifications.controller;

import java.util.List;

import org.edgexfoundry.support.domain.notifications.Transmission;
import org.springframework.web.bind.annotation.PathVariable;

public interface TransmissionController {

  List<Transmission> findByNotificationSlug(@PathVariable String slug, @PathVariable int limit);

  List<Transmission> findByCreatedDuration(@PathVariable long start, @PathVariable long end,
      @PathVariable int limit);

  List<Transmission> findByCreatedAfter(@PathVariable long start, @PathVariable int limit);

  List<Transmission> findByCreatedBefore(@PathVariable long end, @PathVariable int limit);

  List<Transmission> findEscalatedTransmissions(@PathVariable int limit);

  List<Transmission> findFailedTransmissions(@PathVariable int limit);

  boolean deleteOldSent(@PathVariable long age);

  boolean deleteOldEscalated(@PathVariable long age);

  boolean deleteOldAcknowledged(@PathVariable long age);

  boolean deleteOldFailed(@PathVariable long age);

}
