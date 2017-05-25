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
package org.edgexfoundry;

import org.edgexfoundry.support.notifications.config.GeneralConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableAsync
@EnableMongoAuditing
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
public class EdgeXSupportNotificationsApplication {
	
	@Autowired
	private GeneralConfig generalConfig;

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(EdgeXSupportNotificationsApplication.class, args);
		String welcomeMsg = ctx.getEnvironment().getProperty("app.open.msg");
		System.out.println(welcomeMsg);
	}

	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskScheduler initScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setPoolSize(generalConfig.getSchedulerPoolSize());
		scheduler.initialize();
		return scheduler;
	}

}
