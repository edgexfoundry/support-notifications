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
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.service.TransmissionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transmission")
public class TransmissionController {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());
	
	private final String DOMAIN_NAME = "Transmission";

	@Autowired
	private TransmissionHandler transmissionHandler;
	
	@Autowired
	private GeneralConfig generalConfig;
	
	@RequestMapping(value = "/slug/{slug:.+}/{limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Transmission> findByNotificationSlug(@PathVariable String slug, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Transmission> result;
		try {
			result = transmissionHandler.findByNotificationSlug(slug, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/{start}/{end}/{limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Transmission> findByCreatedDuration(@PathVariable long start, @PathVariable long end, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Transmission> result;
		try {
			result = transmissionHandler.findByCreatedDuration(start, end, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		}
		return result;
	}
	
	@RequestMapping(value = "/start/{start}/{limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Transmission> findByCreatedAfter(@PathVariable long start, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Transmission> result;
		try {
			result = transmissionHandler.findByCreatedAfter(start, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/end/{end}/{limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Transmission> findByCreatedBefore(@PathVariable long end, @PathVariable int limit) {
		checkMaxLimit(limit);
		List<Transmission> result;
		try {
			result = transmissionHandler.findByCreatedBefore(end, limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/escalated/{limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Transmission> findEscalatedTransmissions(@PathVariable int limit) {
		checkMaxLimit(limit);
		List<Transmission> result;
		try {
			result = transmissionHandler.findEscalatedTransmissions(limit);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/failed/{limit}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Transmission> findFailedTransmissions(@PathVariable int limit) {
		checkMaxLimit(limit);
		List<Transmission> result;
		try {
			result = transmissionHandler.findFailedTransmissions(limit);
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
	
	@RequestMapping(value = "/sent/age/{age}", method = RequestMethod.DELETE)
	public boolean deleteOldSent(@PathVariable long age) {
		try {
			transmissionHandler.deleteOldSentTransmissions(age);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@RequestMapping(value = "/escalated/age/{age}", method = RequestMethod.DELETE)
	public boolean deleteOldEscalated(@PathVariable long age) {
		try {
			transmissionHandler.deleteOldEscalatedTransmissions(age);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@RequestMapping(value = "/acknowledged/age/{age}", method = RequestMethod.DELETE)
	public boolean deleteOldAcknowledged(@PathVariable long age) {
		try {
			transmissionHandler.deleteOldAcknowledgedTransmissions(age);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@RequestMapping(value = "/failed/age/{age}", method = RequestMethod.DELETE)
	public boolean deleteOldFailed(@PathVariable long age) {
		try {
			transmissionHandler.deleteOldFailedTransmissions(age);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

}
