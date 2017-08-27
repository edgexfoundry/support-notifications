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

import org.edgexfoundry.support.domain.notifications.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SubscriptionDAO extends MongoRepository<Subscription, String> {

  // @Query("{ '$or': [ { 'subscribedCategories': { '$in': ?0 } }, { 'subscribedLabels': { '$in': ?1
  // } } ] }")
  public List<Subscription> findBySubscribedCategoriesInIgnoreCaseOrSubscribedLabelsInIgnoreCase(
      String[] categories, String[] labels);

  public Subscription findBySlugIgnoreCase(String slug);

  public List<Subscription> findByReceiverLikeIgnoreCase(String receiver);

}
