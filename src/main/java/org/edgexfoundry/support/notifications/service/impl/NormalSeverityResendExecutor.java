/*******************************************************************************
 * Copyright 2016-2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-notifications
 * @author: Cloud Tsai, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.support.notifications.service.impl;

import java.util.List;

import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Transmission;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;
import org.edgexfoundry.support.notifications.GlobalVariables;
import org.edgexfoundry.support.notifications.config.GeneralConfig;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.ResendTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NormalSeverityResendExecutor implements ResendTaskExecutor {

  private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

  @Autowired
  TransmissionDAO transmissionDAO;

  @Autowired
  DistributionCoordinator distributionCoordinator;

  @Autowired
  private GeneralConfig generalConfig;

  @Scheduled(fixedDelayString = "${application.scheduler.normal.resend.duration}")
  @Async
  @Override
  public void startResnding() {
    logger.info("normal severity resend scheduler is triggered.");
    GlobalVariables vars = GlobalVariables.getInstance();
    if (vars.getTheLatestNormalResendProcessed() > vars.getTheLatestNormalTransmissionFailed()) {
      logger.info("there is no failed normal notification. scheduler is ended.");
      return;
    }

    List<Transmission> transmissions;
    try {
      transmissions = transmissionDAO.findByStatusAndResendCountLessThan(TransmissionStatus.FAILED,
          generalConfig.getResendLimit());

      for (Transmission transmission : transmissions) {
        distributionCoordinator.resendViaChannel(transmission);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ServiceException(e);
    }

    vars.setTheLatestNormalResendProcessed(System.currentTimeMillis());

    logger.info("normal severity resend scheduler has processed completely. there are(is) "
        + transmissions.size() + " failed transmission(s)");
  }

}
