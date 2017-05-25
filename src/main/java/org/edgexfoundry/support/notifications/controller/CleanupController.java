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

import org.edgexfoundry.support.notifications.service.CleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cleanup")
public class CleanupController {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
				org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());
	
	@Autowired
	private CleanupService cleanupService;

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<Void> cleanupOld() {
		logger.info("received cleanup request for default age");
		cleanupService.cleanupOld();
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}

	@RequestMapping(value = "/age/{age}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> cleanupOld(@PathVariable long age) {
		logger.info("received cleanup request for age: " + age);
		cleanupService.cleanupOld(age);
		return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
	}

}
