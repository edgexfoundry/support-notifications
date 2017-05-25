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
import org.edgexfoundry.support.domain.notifications.Notification
import org.edgexfoundry.support.domain.notifications.NotificationCategory
import org.edgexfoundry.support.domain.notifications.NotificationSeverity
import org.edgexfoundry.support.notifications.controller.NotificationController
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
@Title("Check Notification slug integrity checking acts as expected")
@Subject(NotificationController.class)
@SpringApplicationConfiguration(classes = EdgeXSupportNotificationsApplication.class)
@WebIntegrationTest
class NotificationSlugIntegritySpec extends Specification {
	
	String sampleSlug
	
	@Autowired
	NotificationController controller

	def setup() {
		sampleSlug = "important-alert"
		
		Notification notification = createNewNotification()
		
		controller.receiveNotification(notification)
	}
	
	private Notification createNewNotification() {
		String testDataPrefix = this.class.getName()
		
		Notification notification = new Notification()
		notification.with {
			slug = sampleSlug
			sender = "$testDataPrefix-sender"
			category = NotificationCategory.SW_HEALTH
			severity = NotificationSeverity.NORMAL
			content = "$testDataPrefix-content"
			description = "$testDataPrefix-description"
			labels = ["test","spec"]
		}
		
		return notification
	}

	@Timeout(60)
	def "slug cannot be null or empty String when creating a Notification"(String invalidSlug) {
		given: "a notification with invalid slug"
		Notification notification = createNewNotification()
		notification.slug = invalidSlug
		
		and: "a running NotificationController"
		// created by Spring Autowired
		
		when: "pass the new Notification object to NotificationController"
		controller.receiveNotification(notification)
		
		then: "get ClientException because of invalid slug"
		thrown(ClientException)
		
		where: "invalid slug values"
		invalidSlug || _
		null || _
		"" || _
	}
	
	@Timeout(60)
	def "slug is case insensitive, so the same slug of Notification with different case cannot be created again"(String duplicatedSlug) {
		given: "a Notification with invalid slug"
		Notification notification = createNewNotification()
		notification.slug = duplicatedSlug
		
		and: "a running NotificationController"
		// created by Spring Autowired
		
		when: "pass the new Notification object to NotificationController"
		controller.receiveNotification(notification)
		println notification.toString()
		
		then: "get DataValidationException because of duplicated slug"
		thrown(DataValidationException)
		
		where: "duplicated slug values"
		duplicatedSlug || _
		"Important-alert" || _
		"important-Alert" || _
		"important-aLERt" || _
		"impOrtant-alert" || _
		"imporTAnt-alert" || _
		"Important-Alert" || _
		"IMPORTANT-ALERT" || _
	}
	
	@Timeout(60)
	def "find Notification by slug function can retrieve the same Notification when slug is in different case"(String findingSlug) {	
		given: "a running NotificationController"
		// created by Spring Autowired
		
		when: "pass the new Notification object to NotificationController"
		Notification foundNotification = controller.findBySlug(findingSlug)
		
		then: "get ClientException because of invalid slug"
		foundNotification.slug == sampleSlug
		
		where: "finding slug values"
		findingSlug || _
		"Important-alert" || _
		"important-Alert" || _
		"important-aLERt" || _
		"impOrtant-alert" || _
		"imporTAnt-alert" || _
		"Important-Alert" || _
		"IMPORTANT-ALERT" || _
	}
	
	def cleanup() {
		controller.deleteBySlug(sampleSlug)
	}
	
}
