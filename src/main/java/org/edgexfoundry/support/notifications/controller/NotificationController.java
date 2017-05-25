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
package org.edgexfoundry.support.notifications.controller;

import java.util.List;

import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.LimitExceededException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.service.NotificationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
	
	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());
	
	private final String DOMAIN_NAME = "Notification";
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private GeneralConfig generalConfig;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<String> receiveNotification(@RequestBody Notification notification) {
		try {
			checkSlugIntegrity(notification.getSlug());
			notificationHandler.receiveNotification(notification);
			return new ResponseEntity<String>(notification.getSlug(), HttpStatus.ACCEPTED);
		} catch (DataValidationException | ClientException | ServiceException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	private void checkSlugIntegrity(String slug) {		
		if (slug == null || slug.isEmpty()) {
			String errMsg = "slug is null or empty.";
			logger.info(errMsg);
			throw new ClientException(errMsg);
		}
		
		Notification n = notificationHandler.findBySlug(slug);
		if (n != null) {
			String errMsg = "duplicated notification slug: " + slug;
			logger.info(errMsg);
			throw new DataValidationException(errMsg);
		}
	}
	
	@RequestMapping(value = "/slug/{slug:.+}", method = RequestMethod.GET)
	public Notification findBySlug(@PathVariable String slug) {
		Notification result;
		try {
			result = notificationHandler.findBySlug(slug);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		if(result == null) {
			throw new NotFoundException(Notification.class.toString(), slug);
		}
		return result;
	}
	
	@RequestMapping(value = "/sender/{sender:.+}/{limit}", method = RequestMethod.GET)
	public List<Notification> findBySender(@PathVariable String sender, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Notification> result;
		try {
			result = notificationHandler.searchBySender(sender, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/{start}/{end}/{limit}", method = RequestMethod.GET)
	public List<Notification> findByCreatedDuration(@PathVariable long start, @PathVariable long end, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Notification> result;
		try {
			result = notificationHandler.findByCreatedDuration(start, end, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/start/{start}/{limit}", method = RequestMethod.GET)
	public List<Notification> findByCreatedAfter(@PathVariable long start, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Notification> result;
		try {
			result = notificationHandler.findByCreatedAfter(start, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/end/{end}/{limit}", method = RequestMethod.GET)
	public List<Notification> findByCreatedBefore(@PathVariable long end, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Notification> result;
		try {
			result = notificationHandler.findByCreatedBefore(end, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}

	@RequestMapping(value = "/labels/{labels}/{limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Notification> findInLabels(@PathVariable String[] labels, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Notification> result;
		try {
			result = notificationHandler.findInLabels(labels, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/new/{limit:.+}", method = RequestMethod.GET)
	public List<Notification> findNewNotifications(@PathVariable int limit) {
		checkMaxLimit(limit);
		List<Notification> result;
		try {
			result = notificationHandler.findNewNotifications(limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	private void checkMaxLimit(int limit) {
		if (limit > generalConfig.getReadLimit()) {
			LimitExceededException e = new LimitExceededException(DOMAIN_NAME);
			logger.info(e.getMessage());
			throw e;
		}
	}
	
	@RequestMapping(value = "/slug/{slug:.+}", method = RequestMethod.DELETE)
	public boolean deleteBySlug(@PathVariable String slug) {
		try {
			notificationHandler.deleteNotificationAndTransmissionsBySlug(slug);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@RequestMapping(value = "/age/{age}", method = RequestMethod.DELETE)
	public boolean deleteOld(@PathVariable long age) {
		try {
			notificationHandler.deleteOldProcessedNotificationsAndTransmissions(age);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
}
