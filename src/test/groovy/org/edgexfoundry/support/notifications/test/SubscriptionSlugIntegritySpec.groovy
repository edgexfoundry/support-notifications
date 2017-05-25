/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice:  support-notifications
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/
 package org.edgexfoundry.support.notifications.test

import org.junit.experimental.categories.Category
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest

import org.edgexfoundry.EdgeXSupportNotificationsApplication
import org.edgexfoundry.exception.controller.ClientException
import org.edgexfoundry.exception.controller.DataValidationException
import org.edgexfoundry.support.domain.notifications.Subscription
import org.edgexfoundry.support.notifications.controller.SubscriptionController
import org.edgexfoundry.test.category.RequiresMongoDB

import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Timeout
import spock.lang.Title

@Category([ RequiresMongoDB.class ])
@Narrative(""" Slug is the unique key in Notification and Subscription domain model. 
 It is a kind of an user-defined identification with meaning, and it is a required file.
 Thus, it cannot be null or empty String when creating a Notification or Subscription.
 However, it is case insensitive for user friendly design.
""")
@Title("Check Subscription slug integrity checking acts as expected")
@Subject(SubscriptionController.class)
@SpringApplicationConfiguration(classes = EdgeXSupportNotificationsApplication.class)
@WebIntegrationTest
class SubscriptionSlugIntegritySpec extends Specification {
	
	String sampleSlug
	
	@Autowired
	SubscriptionController controller

	def setup() {
		sampleSlug = "system-admin"
		
		Subscription subscription = createNewSubscription()
		
		controller.createSubscription(subscription)
	}
	
	private Subscription createNewSubscription() {
		String testDataPrefix = this.class.getName()
		
		Subscription subscription = new Subscription()
		subscription.with {
			slug = sampleSlug
			receiver = "$testDataPrefix-receiver"
			description = "$testDataPrefix-description"
			subscribedCategories = []
			subscribedLabels = ["test", "groovy"]
			channels = []
		}
		
		return subscription
	}

	@Timeout(60)
	def "slug cannot be null or empty String when creating a Subscription"(String invalidSlug) {
		given: "a Subscription with invalid slug"
		Subscription subscription = createNewSubscription()
		subscription.slug = invalidSlug
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "pass the new Subscription object to SubscriptionController"
		controller.createSubscription(subscription)
		
		then: "get ClientException because of invalid slug"
		thrown(ClientException)
		
		where: "invalid slug values"
		invalidSlug || _
		null || _
		"" || _
	}
	
	@Timeout(60)
	def "slug is case insensitive, so the same slug of Subscription with different case cannot be created again"(String duplicatedSlug) {
		given: "a subscription with invalid slug"
		Subscription subscription = createNewSubscription()
		subscription.slug = duplicatedSlug
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "pass the new Subscription object to SubscriptionController"
		controller.createSubscription(subscription)
		
		then: "get DataValidationException because of duplicated slug"
		thrown(DataValidationException)
		
		where: "duplicated slug values"
		duplicatedSlug || _
		"System-admin" || _
		"System-Admin" || _
		"SysTem-admin" || _
		"SYSTEM-ADMIN" || _
		"SystEM-Admin" || _
		"System-AdMIN" || _
		"SyStem-AdmIn" || _
	}
	
	@Timeout(60)
	def "find Notification by slug function can retrieve the same Notification when slug is in different case"(String findingSlug) {	
		given: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "pass the new Subscription object to SubscriptionController"
		Subscription foundSubscription = controller.findBySlug(findingSlug)
		
		then: "get ClientException because of invalid slug"
		foundSubscription.slug == sampleSlug
		
		where: "finding slug values"
		findingSlug || _
		"System-admin" || _
		"System-Admin" || _
		"SysTem-admin" || _
		"SYSTEM-ADMIN" || _
		"SystEM-Admin" || _
		"System-AdMIN" || _
		"SyStem-AdmIn" || _
	}
	
	def cleanup() {
		controller.deleteBySlug(sampleSlug)
	}
	
}
