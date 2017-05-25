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
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Subscription;
import org.edgexfoundry.support.notifications.service.SubscriptionHandler;
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
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

	@Autowired
	private SubscriptionHandler subscriptionHandler;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createSubscription(@RequestBody Subscription subscription) {
		try {
			checkSlugIntegrity(subscription.getSlug());
			subscriptionHandler.createSubscription(subscription);
			return new ResponseEntity<String>(subscription.getSlug(), HttpStatus.CREATED);
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
		
		Subscription s = subscriptionHandler.findBySlug(slug);
		if (s != null) {
			String errMsg = "duplicated subscription slug: " + slug;
			logger.info(errMsg);
			throw new DataValidationException(errMsg);
		}
	}
	
	@RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean updateSubscription(@RequestBody Subscription subscription) {
		try {
			subscriptionHandler.updateSubscription(subscription);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Subscription> listAll() {
		List<Subscription> result;
		try {
			result = subscriptionHandler.listAll();
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}

	@RequestMapping(value = "/slug/{slug:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Subscription findBySlug(@PathVariable String slug) {
		Subscription result;
		try {
			result = subscriptionHandler.findBySlug(slug);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		if(result == null) {
			throw new NotFoundException(Subscription.class.toString(), slug);
		}
		return result;
	}

	@RequestMapping(value = "/categories/{categories}/labels/{labels}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Subscription> findInSubscribedCategoriesOrSubscribedLabels(
			@PathVariable String[] categories, @PathVariable String[] labels) {
		List<Subscription> result;
		try {
			result = subscriptionHandler.findInSubscribedCategoriesOrSubscribedLabels(categories, labels);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/categories/{categories}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Subscription> findInSubscribedCategories(
			@PathVariable String[] categories) {
		List<Subscription> result;
		try {
			result = subscriptionHandler.findInSubscribedCategoriesOrSubscribedLabels(categories, new String[] {});
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/labels/{labels}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Subscription> findInSubscribedLabels(@PathVariable String[] labels) {
		List<Subscription> result;
		try {
			result = subscriptionHandler.findInSubscribedCategoriesOrSubscribedLabels(new String[] {}, labels);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/receiver/{receiver:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Subscription> searchByReceiver(@PathVariable String receiver) {
		List<Subscription> result;
		try {
			result = subscriptionHandler.searchByReceiver(receiver);
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
		return result;
	}
	
	@RequestMapping(value = "/slug/{slug:.+}", method = RequestMethod.DELETE)
	public boolean deleteBySlug(@PathVariable String slug) {
		try {
			subscriptionHandler.deleteBySlug(slug);
			return true;
		} catch (NotFoundException | DataValidationException | ServiceException | ClientException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
}
