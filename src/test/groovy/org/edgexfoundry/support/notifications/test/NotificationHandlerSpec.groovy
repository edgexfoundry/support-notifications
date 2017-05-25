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

import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

import org.edgexfoundry.support.domain.notifications.Notification
import org.edgexfoundry.support.domain.notifications.NotificationCategory
import org.edgexfoundry.support.domain.notifications.NotificationSeverity
import org.edgexfoundry.support.domain.notifications.NotificationStatus
import org.edgexfoundry.support.notifications.dao.NotificationDAO
import org.edgexfoundry.support.notifications.service.DistributionCoordinator
import org.edgexfoundry.support.notifications.service.NotificationHandler
import org.edgexfoundry.support.notifications.service.impl.NotificationHandlerImpl
import org.edgexfoundry.test.category.RequiresNone

@Category([ RequiresNone.class ])
@Narrative(""" Alerts and Notifications Micro Service would provide multiple interfaces for clients, such as 
REST, MQTT, or AMQP.  No matter which interface receives the notification, it will be passed to NotificationHandler 
to determine the further processing. 
""")
@Title("Check the NotificationHandler acts as expected")
@Subject(NotificationHandlerImpl.class)
class NotificationHandlerSpec extends Specification {
	
	Notification notification
	NotificationHandlerImpl handler = new NotificationHandlerImpl() 
	
	def setup() {
		String testDataPrefix = this.class.getName()
		notification = new Notification()
		notification.with {
			slug = "$testDataPrefix-slug"
			sender = "$testDataPrefix-sender"
			category = NotificationCategory.SW_HEALTH
			severity = NotificationSeverity.NORMAL
			content = "$testDataPrefix-content"
			description = "$testDataPrefix-description"
			labels = ["test","spec"]
		}
	}

	def "a new critical notification is passed to NotificationHandler"() {
		given: "a new notification whose severity = critical"
		notification.severity = NotificationSeverity.CRITICAL
		
		and: "a persistence DAO"
		NotificationDAO notificationDAO = Mock(NotificationDAO)
		handler.setNotificationDAO(notificationDAO)
		
		and: "a DistributionCoordinator servcie"
		DistributionCoordinator distributionCoordinator = Mock(DistributionCoordinator)
		handler.setDistributionCoordinator(distributionCoordinator)
		
		when: "the notification is passed to NotificationHandler"
		handler.receiveNotification(notification)
		
		then: "the notification should be persisted via DAO"
		1 * notificationDAO.insert(notification) >> notification
		
		and: "the notification should be passed to DistributionCoordinator service immediately"
		1 * distributionCoordinator.distribute(notification)
		
		and: "the notification status is changed to PROCESSED and persisted"
		notification.status == NotificationStatus.PROCESSED
		1 * notificationDAO.save(notification) >> notification
		
		and: "no other operations are executed"
		0 * _
	}

	def "a new normal notification is passed to NotificationHandler"() {
		given: "a new notification whose severity = normal"
		notification.severity = NotificationSeverity.NORMAL
		
		and: "a persistence DAO"
		NotificationDAO notificationDAO = Mock(NotificationDAO)
		handler.setNotificationDAO(notificationDAO)
		
		and: "a DistributionCoordinator servcie"
		DistributionCoordinator distributionCoordinator = Mock(DistributionCoordinator)
		handler.setDistributionCoordinator(distributionCoordinator)
		
		when: "the notification is passed to NotificationHandler"
		handler.receiveNotification(notification)
		
		then: "the notification should be persisted via DAO"
		1 * notificationDAO.insert(notification) >> notification
		
		and: "the notification status is still NEW"
		notification.status == NotificationStatus.NEW
		
		and: "no other operations are executed"
		0 * _
	}
	
}
