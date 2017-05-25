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

import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;
import org.edgexfoundry.support.notifications.GlobalVariables;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.TransmissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TransmissionHandlerImpl implements TransmissionHandler {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

	@Autowired
	private TransmissionDAO transmissionDAO;

	@Autowired
	private NotificationDAO notificationDAO;
	
	@Autowired
	private GeneralConfig generalConfig;

	private GlobalVariables globalVariables = GlobalVariables.getInstance();

	@Override
	public List<Transmission> findByCreatedDuration(long start, long end) {
		logger.debug("TransmissionHandler is finding transmissions by created between " + start + " and " + end);
		return transmissionDAO.findByCreatedBetween(start, end);
	}

	@Override
	public List<Transmission> findByCreatedAfter(long start) {
		logger.debug("TransmissionHandler is finding transmissions by created after " + start);
		return transmissionDAO.findByCreatedAfter(start);
	}

	@Override
	public List<Transmission> findByCreatedBefore(long end) {
		logger.debug("TransmissionHandler is finding transmissions by created before " + end);
		return transmissionDAO.findByCreatedBefore(end);
	}

	@Override
	public List<Transmission> findByNotificationSlug(String slug) {
		logger.debug("TransmissionHandler is finding transmissions by notification slug=" + slug);
		Notification notification = notificationDAO.findBySlugIgnoreCase(slug);
		return transmissionDAO.findByNotificationId(notification.getId());
	}

	@Override
	public List<Transmission> findEscalatedTransmissions() {
		logger.debug("TransmissionHandler is finding escalated transmissions");
		return transmissionDAO.findByStatus(TransmissionStatus.ESCALATED);
	}

	@Override
	public List<Transmission> findFailedTransmissions() {
		logger.debug("TransmissionHandler is finding failed transmissions");
		return transmissionDAO.findByStatus(TransmissionStatus.FAILED);
	}

	@Override
	public List<Transmission> findByCreatedDuration(long start, long end, int limit) {
		logger.debug("TransmissionHandler is finding transmissions by created between " + start + " and " + end
				+ " in limit=" + limit);
		PageRequest request = new PageRequest(0, limit,
				new Sort(Sort.Direction.DESC, globalVariables.RECORD_CREATION_FIELD));
		return transmissionDAO.findByCreatedBetween(start, end, request).getContent();
	}

	@Override
	public List<Transmission> findByCreatedAfter(long start, int limit) {
		logger.debug("TransmissionHandler is finding transmissions by created after " + start + " in limit=" + limit);
		PageRequest request = new PageRequest(0, limit,
				new Sort(Sort.Direction.DESC, globalVariables.RECORD_CREATION_FIELD));
		return transmissionDAO.findByCreatedAfter(start, request).getContent();
	}

	@Override
	public List<Transmission> findByCreatedBefore(long end, int limit) {
		logger.debug("TransmissionHandler is finding transmissions by created before " + end + " in limit=" + limit);
		PageRequest request = new PageRequest(0, limit,
				new Sort(Sort.Direction.DESC, globalVariables.RECORD_CREATION_FIELD));
		return transmissionDAO.findByCreatedBefore(end, request).getContent();
	}

	@Override
	public List<Transmission> findByNotificationSlug(String slug, int limit) {
		logger.debug(
				"TransmissionHandler is finding transmissions by notification slug=" + slug + " in limit=" + limit);
		PageRequest request = new PageRequest(0, limit,
				new Sort(Sort.Direction.DESC, globalVariables.RECORD_CREATION_FIELD));
		Notification notification = notificationDAO.findBySlugIgnoreCase(slug);
		if (notification == null) {
			throw new NotFoundException(Notification.class.toString(), slug);
		}
		return transmissionDAO.findByNotificationId(notification.getId(), request).getContent();
	}

	@Override
	public List<Transmission> findEscalatedTransmissions(int limit) {
		logger.debug("TransmissionHandler is finding escalated transmissions" + " in limit=" + limit);
		PageRequest request = new PageRequest(0, limit,
				new Sort(Sort.Direction.DESC, globalVariables.RECORD_CREATION_FIELD));
		return transmissionDAO.findByStatus(TransmissionStatus.ESCALATED, request).getContent();
	}

	@Override
	public List<Transmission> findFailedTransmissions(int limit) {
		logger.debug("TransmissionHandler is finding failed transmissions" + " in limit=" + limit);
		PageRequest request = new PageRequest(0, limit,
				new Sort(Sort.Direction.DESC, globalVariables.RECORD_CREATION_FIELD));
		return transmissionDAO.findByStatus(TransmissionStatus.FAILED, request).getContent();
	}

	@Override
	public void deleteOldSentTransmissions(long age) {
		TransmissionStatus status = TransmissionStatus.SENT;
		deleteByAgeAndStatus(age, status);
	}

	@Override
	public void deleteOldEscalatedTransmissions(long age) {
		TransmissionStatus status = TransmissionStatus.ESCALATED;
		deleteByAgeAndStatus(age, status);
	}

	@Override
	public void deleteOldAcknowledgedTransmissions(long age) {
		TransmissionStatus status = TransmissionStatus.ACKNOWLEDGED;
		deleteByAgeAndStatus(age, status);
	}
	
	private void deleteByAgeAndStatus(long age, TransmissionStatus status) {
		long end = System.currentTimeMillis() - age;
		Date endDate = new Date(end);
		
		try {
			logger.debug("TransmissionHandler is starting deleting "+ status.toString() +" transmissions by notifications modified before " + endDate);
			transmissionDAO.deleteByStatusAndModifiedBefore(status, end);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		logger.debug("Deletion operation by modified before " + endDate + " is completed");
	}
	
	@Override
	public void deleteOldFailedTransmissions(long age) {
		long end = System.currentTimeMillis() - age;
		Date endDate = new Date(end);
		TransmissionStatus status = TransmissionStatus.FAILED;
		
		try {
			logger.debug("TransmissionHandler is starting deleting "+ status.toString() +" transmissions by notifications modified before " + endDate);
			transmissionDAO.deleteByStatusAndResendCountGreaterThanEqualAndModifiedBefore(status, generalConfig.getResendLimit(), end);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		
		logger.debug("Deletion operation by modified before " + endDate + " is completed");
	}

}
