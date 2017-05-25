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
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import spock.lang.Ignore
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject
import spock.lang.Timeout
import spock.lang.Title

import org.edgexfoundry.EdgeXSupportNotificationsApplication
import org.edgexfoundry.exception.controller.NotFoundException
import org.edgexfoundry.support.domain.notifications.NotificationCategory
import org.edgexfoundry.support.domain.notifications.RESTfulChannel
import org.edgexfoundry.support.domain.notifications.Subscription
import org.edgexfoundry.support.notifications.controller.SubscriptionController
import org.edgexfoundry.test.category.RequiresMongoDB

@Category([ RequiresMongoDB.class ])
@Narrative(""" SubscriptionController handles the manipulation requests about Subscription, including 
create, query, update, and delete. 
""")
@Title("Check the SubscriptionController acts as expected")
@Subject(SubscriptionController.class)
@Stepwise
@SpringApplicationConfiguration(classes = EdgeXSupportNotificationsApplication.class)
@WebIntegrationTest
class SubscriptionControllerTest extends Specification {
	
	Subscription subscription
	
	@Autowired
	SubscriptionController controller
	
	@Value("\${server.port}")
	String port
	
	def setup() {
		String testDataPrefix = this.class.getName()
		
		subscription = new Subscription()
		subscription.with {
			slug = "$testDataPrefix-slug"
			receiver = "$testDataPrefix-receiver"
			description = "$testDataPrefix-description"
			subscribedCategories = [NotificationCategory.SW_HEALTH, NotificationCategory.SECURITY]
			subscribedLabels = ["test", "groovy"]
			channels = [ new RESTfulChannel("http://localhost:$port/test/restful") ]
		}
	}
	
	@Timeout(60)
	def "create a new Subscription"() {
		given: "a new Subscription object"
		// created by setup() method
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "pass the new Subscription object to SubscriptionController"
		ResponseEntity<String> response = controller.createSubscription(subscription)
		
		then: "the response status code should be 201 CREATED"
		response.getStatusCode() == HttpStatus.CREATED
		
		and: "the response body should be equal to the slug"
		response.getBody() == subscription.slug
	}
	
	@Timeout(60)
	def "find an existing Subscription by slug"() {
		given: "an existing Subscription slug"
		String slug = subscription.slug
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "find the existing Subscription by slug from database"
		Subscription foundSubscription = controller.findBySlug(slug)
		
		then: "the found Subscription is not null"
		foundSubscription
		
		and: "the slug of found Subscription should equal to the condition"
		foundSubscription.slug == slug
		
		and: "the created date should be larger than 0"
		foundSubscription.created > 0L
	}
	
	@Timeout(60)
	def "update an existing Subscription by slug"() {
		given: "an existing Subscription slug"
		String slug = subscription.slug
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "find the existing Subscription by slug from database"
		Subscription foundSubscription = controller.findBySlug(slug)
		
		then: "the found Subscription is not null"
		foundSubscription
		
		when: "change some properties and update the found Subscription"
		String updatedReceiver = "updated receiver"
		String updatedDesc = "updated description"
		foundSubscription.with {
			receiver = updatedReceiver
			description = updatedDesc
		}
		boolean updateResult = controller.updateSubscription(foundSubscription)
		
		then: "the update result should be true"
		updateResult
		
		when: "find the existing Subscription by slug from database again"
		Subscription updatedSubscription = controller.findBySlug(slug)
		
		then: "the found Subscription is not null"
		updatedSubscription
		
		and: "the data should be updated"
		with (updatedSubscription) {
			receiver == updatedReceiver
			description == updatedDesc
		}
		
		and: "the modified time should be larger than created time"
		foundSubscription.modified > foundSubscription.created
	}
	
	@Timeout(60)
	def "delete the Subscription created in this test"() {
		given: "an existing Subscription slug"
		String slug = subscription.slug
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "delete the existing Subscription by slug from database"
		boolean deleted = controller.deleteBySlug(slug)
		
		then: "the flag of deletion should be true"
		deleted
	}
	
	@Timeout(60)
	def "the deleted Sbuscription should not be found"() {
		given: "a deleted Subscription slug"
		String slug = subscription.slug
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "find the deleted Subscription by slug from database"
		controller.findBySlug(slug)
		
		then: "the NotFoundException should be thrown"
		thrown(NotFoundException)
	}

}
