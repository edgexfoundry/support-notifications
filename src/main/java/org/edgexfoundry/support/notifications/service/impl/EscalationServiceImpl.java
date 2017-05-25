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
package org.edgexfoundry.support.notifications.service.impl;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationStatus;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.SubscriptionDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.EscalationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = BeanDefinition.SCOPE_PROTOTYPE)
public class EscalationServiceImpl implements EscalationService {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

	@Autowired
	private DistributionCoordinator distributionCoordinator;

	@Autowired
	private NotificationDAO notificationDAO;
	
	@Autowired
	private SubscriptionDAO subscriptionDAO;

	@Async
	@Override
	public void escalate(Transmission transmission) {
		if (transmission == null) {
			logger.error("EscalationService received a null object");
			throw new DataValidationException("Transmission is null");
		}

		logger.warn("EscalationService is triggered by " + transmission.toString());

		Subscription subscription;
		Notification escalatedNotification;
		try {
			subscription = subscriptionDAO.findBySlugIgnoreCase(ESCALATION_SUBSCRIPTION_SLUG);
			escalatedNotification = createEscalatedNotification(transmission);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		distributionCoordinator.sendViaChannel(escalatedNotification, subscription);
	}

	private Notification createEscalatedNotification(Transmission transmission) {
		Notification notification = transmission.getNotification();
		Notification escalatedNotification = new Notification();
		escalatedNotification.setSlug(ESCALATION_PREFIX + notification.getSlug());
		escalatedNotification.setSender(ESCALATION_PREFIX + notification.getSender());
		escalatedNotification.setCategory(notification.getCategory());
		escalatedNotification.setSeverity(notification.getSeverity());
		escalatedNotification.setContent(String.format("%s %s %n" + notification.getContent(),
				ESCALATION_CONTENT_NOTICE, transmission.toString()));
		escalatedNotification.setDescription(notification.getDescription());
		escalatedNotification.setStatus(NotificationStatus.ESCALATED);
		escalatedNotification.setLabels(notification.getLabels());
		escalatedNotification = notificationDAO.insert(escalatedNotification);
		return escalatedNotification;
	}

}
