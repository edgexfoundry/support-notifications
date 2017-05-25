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
package org.edgexfoundry.support.notifications.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("application.general")
public class GeneralConfig {

	private int readLimit;
	private int resendLimit;
	private int schedulerPoolSize;

	public int getReadLimit() {
		return readLimit;
	}

	public void setReadLimit(int readLimit) {
		this.readLimit = readLimit;
	}

	public int getResendLimit() {
		return resendLimit;
	}

	public void setResendLimit(int resendLimit) {
		this.resendLimit = resendLimit;
	}

	public int getSchedulerPoolSize() {
		return schedulerPoolSize;
	}

	public void setSchedulerPoolSize(int schedulerPoolSize) {
		this.schedulerPoolSize = schedulerPoolSize;
	}

}
