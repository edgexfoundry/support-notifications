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

import java.net.URI;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.RESTfulChannel;
import org.edgexfoundry.support.domain.notifications.TransmissionRecord;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service("RESTfulSendingService")
public class RESTfulSendingService extends AbstractSendingService {

  private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

  @Override
  TransmissionRecord sendToReceiver(Notification notification, Channel channel) {

    RESTfulChannel restfulChannel = (RESTfulChannel) channel;
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(notification.getContentType() == null ? MediaType.TEXT_PLAIN
        : MediaType.parseMediaType(notification.getContentType()));
    HttpEntity<String> request = new HttpEntity<>(notification.getContent(), headers);

    TransmissionRecord record = new TransmissionRecord();

    logger.debug("sending notification to " + restfulChannel.getUrl() + " by HTTP "
        + restfulChannel.getHttpMethod() + " method");

    try {
      record.setSent(System.currentTimeMillis());
      ResponseEntity<String> response = restTemplate.exchange(URI.create(restfulChannel.getUrl()),
          HttpMethod.resolve(restfulChannel.getHttpMethod().toString()), request, String.class);

      logger.debug("got response status code: " + response.getStatusCode() + " with content: "
          + response.getBody());

      record.setStatus(TransmissionStatus.SENT);
      record.setResponse(response.getBody());
    } catch (RestClientException e) {
      logger.error(e.getMessage(), e);
      record.setStatus(TransmissionStatus.FAILED);
      record.setResponse(e.getMessage());
    }

    return record;
  }

  @Override
  protected void checkParameters(Notification notification, Channel channel) {
    super.checkParameters(notification, channel);

    if (!(channel instanceof RESTfulChannel)) {
      logger.error("RESTfulSendingService got an incorrect channel: " + channel);
      throw new DataValidationException(channel + " is not an RESTful channel");
    }

    RESTfulChannel restfulChannel = (RESTfulChannel) channel;

    if (restfulChannel.getUrl() == null || restfulChannel.getHttpMethod() == null) {
      logger.error("RESTfulSendingService got an incorrect channel: " + channel);
      throw new DataValidationException("RESTfulChannel contains null properties");
    }

  }

}
