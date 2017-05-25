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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import org.edgexfoundry.EdgeXSupportNotificationsApplication
import org.edgexfoundry.exception.controller.NotFoundException
import org.edgexfoundry.support.domain.notifications.Notification
import org.edgexfoundry.support.domain.notifications.NotificationCategory
import org.edgexfoundry.support.domain.notifications.NotificationSeverity
import org.edgexfoundry.support.domain.notifications.NotificationStatus
import org.edgexfoundry.support.notifications.controller.NotificationController
import org.edgexfoundry.test.category.RequiresMongoDB

import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject
import spock.lang.Timeout
import spock.lang.Title

@Category([ RequiresMongoDB.class ])
@Narrative(""" NotifcationController handles the manipulation requests about Notification, including
receive, query, and delete. 
""")
@Title("Check the NotifcationController acts as expected")
@Subject(NotificationController.class)
@Stepwise
@SpringApplicationConfiguration(classes = EdgeXSupportNotificationsApplication.class)
@WebIntegrationTest
class NotificationControllerTest extends Specification {
	
	static final int RESULT_LIMIT = 10
	Notification normalNotification, criticalNotification
	String testDataPrefix, testSender
	
	@Autowired
	NotificationController controller
	
	def setup() {
		testDataPrefix = this.class.getName()
		testSender = "$testDataPrefix-sender"
		
		normalNotification = new Notification()
		normalNotification.with {
			slug = "$testDataPrefix-normal-slug"
			sender = testSender
			category = NotificationCategory.SW_HEALTH
			severity = NotificationSeverity.NORMAL
			content = "$testDataPrefix-content"
			description = "$testDataPrefix-description"
			labels = ["test","spec"]
		}
		
		criticalNotification = new Notification()
		criticalNotification.with {
			slug = "$testDataPrefix-critical-slug"
			sender = testSender
			category = NotificationCategory.SW_HEALTH
			severity = NotificationSeverity.CRITICAL
			content = "$testDataPrefix-content"
			description = "$testDataPrefix-description"
			labels = ["test","spec"]
		}
	}
	
	@Timeout(60)
	def "receive 2 new Notifications, including the normal and critical ones"() {
		given: "2 new Notification objects"
		// created by setup() method
		
		and: "a running NotificationController"
		// created by Spring Autowired
		
		when: "pass the new Notification objects to NotificationController"
		ResponseEntity<String> responseN = controller.receiveNotification(normalNotification)
		ResponseEntity<String> responseC = controller.receiveNotification(criticalNotification)		
		
		then: "the response status code should be 202 ACCEPTED"
		responseN.getStatusCode() == HttpStatus.ACCEPTED
		responseC.getStatusCode() == HttpStatus.ACCEPTED
		
		and: "the response body should be equal to the slug"
		responseN.getBody() == normalNotification.slug
		responseC.getBody() == criticalNotification.slug
	}
	
	@Timeout(60)
	def "find existing Notifications by sender"() {
		given: "an existing sender"
		String sender = testSender
		
		and: "a running NotificationController"
		// created by Spring Autowired
		
		when: "find the existing Notifications by sender from database"
		List<Notification> foundNotifications = controller.findBySender(sender, RESULT_LIMIT)
		
		then: "there are 2 Notifications should be found"
		foundNotifications // check not null
		foundNotifications.size() == 2
		
		and: "the 2 slugs of found Notifications should equal to the test data"
		Notification foundN, foundC
		foundNotifications.each {
			switch (it.slug) {
				case normalNotification.slug:
					foundN = it
					break
				case criticalNotification.slug:
					foundC = it
			}
		}
		foundN // check not null
		foundC // check not null
		
		and: "the status of the critial Notifications should be PROCESSED"
		foundC.status == NotificationStatus.PROCESSED
		
		and: "the created date of found Notifications should be larger than 0"
		foundN.created > 0L
		foundC.created > 0L
	}
	
	@Timeout(60)
	def "delete the Notifications created in this test"() {
		given: "the existing Notification slugs"
		String slugN = normalNotification.slug
		String slugC = criticalNotification.slug
		
		and: "a running NotificationController"
		// created by Spring Autowired
		
		when: "delete the existing Notifications by slug from database"
		boolean deletedN = controller.deleteBySlug(slugN)
		boolean deletedC = controller.deleteBySlug(slugC)
		
		then: "the flags of deletion should be both true"
		deletedN || deletedC
	}
	
	@Timeout(60)
	def "the deleted Notifications should not be found"() {
		given: "the existing Notification slugs"
		String slugN = normalNotification.slug
		String slugC = criticalNotification.slug
		
		and: "a running NotificationController"
		// created by Spring Autowired
		
		when: "find the deleted normal Notification by slug from database"
		controller.findBySlug(slugN)
		
		then: "the NotFoundException should be thrown"
		thrown(NotFoundException)
		
		when: "find the deleted critical Notification by slug from database"
		controller.findBySlug(slugC)
		
		then: "the NotFoundException should be thrown"
		thrown(NotFoundException)
	}

}
