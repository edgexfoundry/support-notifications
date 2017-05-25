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

import java.util.List;

import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.support.domain.notifications.NotificationStatus;
import org.edgexfoundry.support.notifications.GlobalVariables;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.DistributionTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NormalSeverityDistributionExecutor implements DistributionTaskExecutor {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

	@Autowired
	NotificationDAO notificationDAO;

	@Autowired
	DistributionCoordinator distributionCoordinator;

	@Scheduled(fixedDelayString = "${application.scheduler.normal.duration}")
	@Async
	@Override
	public void startDistributing() {
		logger.info("normal severity scheduler is triggered.");
		GlobalVariables vars = GlobalVariables.getInstance();
		if (vars.getTheLatestNormalSchedulerProcessed() > vars.getTheLatestNormalNotificationCreated()) {
			logger.info("there is no new normal notification. scheduler is ended.");
			return;
		}

		List<Notification> notifications;
		try {
			notifications = notificationDAO.findBySeverityAndStatus(NotificationSeverity.NORMAL,
					NotificationStatus.NEW);

			for (Notification notification : notifications) {
				distributionCoordinator.distribute(notification);
				notification.setStatus(NotificationStatus.PROCESSED);
				notificationDAO.save(notification);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}

		vars.setTheLatestNormalSchedulerProcessed(System.currentTimeMillis());

		logger.info("normal severity scheduler has processed completely. there are(is) " + notifications.size()
				+ " new notificaion(s)");
	}

}
