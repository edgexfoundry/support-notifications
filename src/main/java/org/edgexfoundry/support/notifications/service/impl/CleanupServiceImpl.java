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

import java.util.Date;
import java.util.List;

import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.CleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class CleanupServiceImpl implements CleanupService {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

	@Autowired
	private NotificationDAO notificationDAO;
	
	@Autowired
	private TransmissionDAO transmissionDAO;

	@Value("${application.cleanup.defaultAge}")
	private long defaultAge;

	@Async
	@Override
	public void cleanupOld() {
		logger.debug("start async cleanup process for default age");
		doCleanup(defaultAge);
		logger.debug("completed async cleanup process for default age");
	}

	@Async
	@Override
	public void cleanupOld(long age) {
		logger.debug("start async cleanup process for age: " + age);
		doCleanup(age);
		logger.debug("completed async cleanup process for age: " + age);
	}
	
	private void doCleanup(long age){
		long end = System.currentTimeMillis() - age;
		Date endDate = new Date(end);

		logger.debug("CleanupService is starting deleting notifications and transmissions by notifications modified before " + endDate);
		
		List<Notification> notifications = notificationDAO.findByModifiedBefore(end);
		
		try {
			logger.debug("CleanupService is starting deleting transmissions by notifications modified before " + endDate);
			transmissionDAO.deleteByNotificationIn(notifications);
			
			logger.debug("CleanupService is starting deleting notifications by notifications modified before " + endDate);
			notificationDAO.delete(notifications);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		logger.debug("Cleanup operation by notifications modified before " + endDate + " is completed");
	}

}
