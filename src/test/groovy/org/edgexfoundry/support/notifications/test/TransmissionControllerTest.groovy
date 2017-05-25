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

import org.edgexfoundry.EdgeXSupportNotificationsApplication
import org.edgexfoundry.exception.controller.NotFoundException
import org.edgexfoundry.support.domain.notifications.ChannelType
import org.edgexfoundry.support.domain.notifications.Notification
import org.edgexfoundry.support.domain.notifications.NotificationCategory
import org.edgexfoundry.support.domain.notifications.NotificationSeverity
import org.edgexfoundry.support.domain.notifications.RESTfulChannel
import org.edgexfoundry.support.domain.notifications.Subscription
import org.edgexfoundry.support.domain.notifications.Transmission
import org.edgexfoundry.support.domain.notifications.TransmissionStatus
import org.edgexfoundry.support.notifications.controller.NotificationController
import org.edgexfoundry.support.notifications.controller.SubscriptionController
import org.edgexfoundry.support.notifications.controller.TransmissionController
import org.edgexfoundry.support.notifications.test.controller.RESTfulCallbackTest
import org.edgexfoundry.test.category.RequiresMongoDB

import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject
import spock.lang.Timeout
import spock.lang.Title

@Category([ RequiresMongoDB.class ])
@Narrative(""" TransmissionController handles the query and deletion requests about Transmission. 
""")
@Title("Check the TransmissionController acts as expected")
@Subject(TransmissionController.class)
@Stepwise
@SpringApplicationConfiguration(classes = EdgeXSupportNotificationsApplication.class)
@WebIntegrationTest
class TransmissionControllerTest extends Specification {

	static final int RESULT_LIMIT = 10
	Notification criticalNotification
	Subscription subscription
	String testDataPrefix

	@Autowired
	NotificationController notificationController

	@Autowired
	SubscriptionController subscriptionController

	@Autowired
	TransmissionController transmissionController

	@Value("\${server.port}")
	String port

	def setup() {
		testDataPrefix = this.class.getName()

		subscription = new Subscription()
		subscription.with {
			slug = "$testDataPrefix-slug"
			receiver = "$testDataPrefix-receiver"
			description = "$testDataPrefix-description"
			subscribedCategories = []
			subscribedLabels = ["testTransmission"]
			channels = [new RESTfulChannel("http://localhost:$port/test/restful")]
		}

		criticalNotification = new Notification()
		criticalNotification.with {
			slug = "$testDataPrefix-critical-slug"
			sender = "$testDataPrefix-sender"
			category = NotificationCategory.SW_HEALTH
			severity = NotificationSeverity.CRITICAL
			content = "$testDataPrefix-content"
			description = "$testDataPrefix-description"
			labels = ["testTransmission", "spec"]
		}
	}

	@Timeout(60)
	def "create a new Subscription for Transmission test"() {
		given: "a new Subscription object"
		// created by setup() method

		and: "a running SubscriptionController"
		// created by Spring Autowired

		when: "pass the new Subscription object to SubscriptionController"
		ResponseEntity<String> response = subscriptionController.createSubscription(subscription)

		then: "the response status code should be 201 CREATED"
		response.getStatusCode() == HttpStatus.CREATED

		and: "the response body should be equal to the slug"
		response.getBody() == subscription.slug
	}

	@Timeout(60)
	def "receive a new critical Notification for Transmission test"() {
		given: "a new critical Notification objects"
		// created by setup() method

		and: "a running NotificationController"
		// created by Spring Autowired

		when: "pass a new critical Notification object to NotificationController"
		ResponseEntity<String> response = notificationController.receiveNotification(criticalNotification)

		then: "the response status code should be 202 ACCEPTED"
		response.getStatusCode() == HttpStatus.ACCEPTED

		and: "the response body should be equal to the slug"
		response.getBody() == criticalNotification.slug
	}

	@Timeout(60)
	def "after receiving a new critical Notification, there should be 1 transmission" () {
		given: "confirm RESTfulCallbackTest is called"
		while(!RESTfulCallbackTest.IS_CALLED) {
			sleep(1000)
		}

		and: "the slug of the Notification"
		String slug = criticalNotification.slug
		
		and: "a running TransmissionController"
		// created by Spring Autowired

		when: "find transmissions by the notification slug"
		List<Transmission> foundTransmissions = transmissionController.findByNotificationSlug(slug, RESULT_LIMIT)
		
		then: "there should be 1 Transmission found"
		foundTransmissions //check not null
		foundTransmissions.size() == 1
		
		and: "the data in the Transmission should be correct"
		Transmission transmission = foundTransmissions[0]
		with (transmission) {
			receiver == subscription.receiver
			channel.type == ChannelType.REST
			status == TransmissionStatus.SENT
			resendCount == 0
			records.length == 1
		}
	}
	
	@Timeout(60)
	def "after deleting the Notification, the related Transmissions should also be deleted"() {
		given: "the slug of the Notification"
		String slug = criticalNotification.slug
		
		and: "a running TransmissionController and a running NotificationController"
		// created by Spring Autowired
		
		when: "delete the Notification by its slug"
		boolean notifcationDeleted = notificationController.deleteBySlug(slug)
		
		then: "the flags of deletion should be both true"
		notifcationDeleted
		
		when: "find transmissions by the notification slug"
		List<Transmission> foundTransmissions = transmissionController.findByNotificationSlug(slug, RESULT_LIMIT)
		
		then: "the query result should be empty"
		thrown(NotFoundException)
	}
	
	@Timeout(60)
	def "delete the Subscription created in this test"() {
		given: "an existing Subscription slug"
		String slug = subscription.slug
		
		and: "a running SubscriptionController"
		// created by Spring Autowired
		
		when: "delete the existing Subscription by slug from database"
		boolean deleted = subscriptionController.deleteBySlug(slug)
		
		then: "the flag of deletion should be true"
		deleted
	}
}
