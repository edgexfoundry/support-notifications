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

import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.notifications.service.SendingService;
import org.springframework.scheduling.annotation.Async;

public class CriticalSeverityResendTask implements Runnable {
	
	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());
	
	private Transmission transmission;
	private SendingService sendingService;

	public CriticalSeverityResendTask(Transmission transmission, SendingService sendingService) {
		this.transmission = transmission;
		this.sendingService = sendingService;
	}

	@Async
	@Override
	public void run() {
		logger.info("critical severity resend scheduler is triggered.");
		logger.debug("the resending transmission is: " + transmission);
		sendingService.resend(transmission);
	}

}
