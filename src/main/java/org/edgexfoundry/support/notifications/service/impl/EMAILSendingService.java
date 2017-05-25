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

import java.util.Arrays;

import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.support.domain.notifications.Channel;
import org.edgexfoundry.support.domain.notifications.EmailChannel;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.TransmissionRecord;
import org.edgexfoundry.support.domain.notifications.TransmissionStatus;
import org.edgexfoundry.support.notifications.config.MailChannelProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service("EMAILSendingService")
public class EMAILSendingService extends AbstractSendingService {

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	//replace above logger with EdgeXLogger below
	private final org.edgexfoundry.support.logging.client.EdgeXLogger logger = 
			org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

	@Autowired
	private MailChannelProperties mailChannelProps;

	@Autowired
	private JavaMailSender mailSender;

	@Override
	TransmissionRecord sendToReceiver(Notification notification, Channel channel) {

		logger.info("EMAILSendingService is starting sending notification: slug=" + notification.getSlug()
				+ " to channel: " + channel.toString());

		EmailChannel emailChannel = (EmailChannel) channel;
		TransmissionRecord record = new TransmissionRecord();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom(mailChannelProps.getSender());
		msg.setSubject(mailChannelProps.getSubject());
		msg.setTo(emailChannel.getMailAddresses());
		msg.setText(notification.getContent());

		logger.debug("sending mail to " + Arrays.toString(emailChannel.getMailAddresses()));
		logger.debug("mail content is: " + notification.getContent());

		try {
			record.setSent(System.currentTimeMillis());
			mailSender.send(msg);
			record.setStatus(TransmissionStatus.SENT);
			record.setResponse("SMTP server received");
		} catch (MailException e) {
			logger.error(e.getMessage(), e);
			record.setStatus(TransmissionStatus.FAILED);
			record.setResponse(e.getMessage());
		}
		
		logger.debug("the mail is sent to the SMTP server");

		return record;
	}

	@Override
	protected void checkParameters(Notification notification, Channel channel) {
		super.checkParameters(notification, channel);

		if (!(channel instanceof EmailChannel)) {
			logger.error("EMAILSendingService got an incorrect channel: " + channel);
			throw new DataValidationException(channel + " is not an email channel");
		}
		
		EmailChannel emailChannel = (EmailChannel) channel;
		if(emailChannel.getMailAddresses() == null) {
			logger.error("EMAILSendingService got a null emaill address");
			throw new DataValidationException(channel + " contains null email address");
		}
	}

}
