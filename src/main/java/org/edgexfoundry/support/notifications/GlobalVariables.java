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
package org.edgexfoundry.support.notifications;

public class GlobalVariables {
	
	private final static GlobalVariables INSTANCE = new GlobalVariables();
	
	public final String RECORD_CREATION_FIELD = "created";
	
	private long theLatestNormalNotificationCreated = 0L;
	private long theLatestNormalSchedulerProcessed = 0L;
	
	private long theLatestNormalTransmissionFailed = 0L;
	private long theLatestNormalResendProcessed = 0L;

	private GlobalVariables() {

	}

	public static GlobalVariables getInstance() {
		return INSTANCE;
	}

	public long getTheLatestNormalNotificationCreated() {
		return theLatestNormalNotificationCreated;
	}

	public void setTheLatestNormalNotificationCreated(long theLatestNormalNotificationCreated) {
		this.theLatestNormalNotificationCreated = theLatestNormalNotificationCreated;
	}

	public long getTheLatestNormalSchedulerProcessed() {
		return theLatestNormalSchedulerProcessed;
	}

	public void setTheLatestNormalSchedulerProcessed(long theLatestNormalSchedulerProcessed) {
		this.theLatestNormalSchedulerProcessed = theLatestNormalSchedulerProcessed;
	}

	public long getTheLatestNormalTransmissionFailed() {
		return theLatestNormalTransmissionFailed;
	}

	public void setTheLatestNormalTransmissionFailed(long theLatestNormalTransmissionFailed) {
		this.theLatestNormalTransmissionFailed = theLatestNormalTransmissionFailed;
	}

	public long getTheLatestNormalResendProcessed() {
		return theLatestNormalResendProcessed;
	}

	public void setTheLatestNormalResendProcessed(long theLatestNormalResendProcessed) {
		this.theLatestNormalResendProcessed = theLatestNormalResendProcessed;
	}

}
