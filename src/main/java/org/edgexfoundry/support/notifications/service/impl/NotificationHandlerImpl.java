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

import static org.edgexfoundry.support.notifications.GlobalVariables.RECORD_CREATION_FIELD;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.edgexfoundry.exception.controller.ClientException;
import org.edgexfoundry.exception.controller.DataValidationException;
import org.edgexfoundry.exception.controller.NotFoundException;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.support.domain.notifications.Notification;
import org.edgexfoundry.support.domain.notifications.NotificationSeverity;
import org.edgexfoundry.support.domain.notifications.NotificationStatus;
import org.edgexfoundry.support.notifications.GlobalVariables;
import org.edgexfoundry.support.notifications.dao.NotificationDAO;
import org.edgexfoundry.support.notifications.dao.TransmissionDAO;
import org.edgexfoundry.support.notifications.service.DistributionCoordinator;
import org.edgexfoundry.support.notifications.service.NotificationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class NotificationHandlerImpl implements NotificationHandler {

  private static final String IN_LIMIT = " in limit=";

  private final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory.getEdgeXLogger(this.getClass());

  @Autowired
  private NotificationDAO notificationDAO;

  @Autowired
  private TransmissionDAO transmissionDAO;

  @Autowired
  private DistributionCoordinator distributionCoordinator;

  @Override
  public void receiveNotification(Notification notification) {
    if (notification == null) {
      logger.error("NotificationHandler received a null object");
      throw new ClientException("Notification is null");
    }

    logger.debug("NotificationHandler has received a new notification: " + notification.toString());

    notification.setStatus(NotificationStatus.NEW);
    try {
      notification = notificationDAO.insert(notification);
    } catch (DuplicateKeyException e) {
      logger.info(e.getMessage(), e);
      throw new DataValidationException("duplicated notification slug: " + notification.getSlug());
    }
    if (notification.getSeverity() == NotificationSeverity.CRITICAL) {
      distributionCoordinator.distribute(notification);
      notification.setStatus(NotificationStatus.PROCESSED);
      notification = notificationDAO.save(notification);
      logger
          .debug("The critical notificaiton has been distributing: slug=" + notification.getSlug());
    } else if (notification.getSeverity() == NotificationSeverity.NORMAL) {
      GlobalVariables.getInstance()
          .setTheLatestNormalNotificationCreated(notification.getCreated());
      logger.debug("The normal notificaiton has been persisted: slug=" + notification.getSlug());
    }
  }

  @Override
  public Notification findBySlug(String slug) {
    logger.debug("NotificationHandler is finding notification by slug=" + slug);
    return notificationDAO.findBySlugIgnoreCase(slug);
  }

  @Override
  public List<Notification> searchBySender(String sender) {
    logger.debug("NotificationHandler is finding notifications by sender=" + sender);
    return notificationDAO.findBySenderLikeIgnoreCase(sender);
  }

  @Override
  public List<Notification> searchBySender(String sender, int limit) {
    logger.debug(
        "NotificationHandler is finding notifications by sender=" + sender + IN_LIMIT + limit);
    PageRequest request =
        new PageRequest(0, limit, new Sort(Sort.Direction.DESC, RECORD_CREATION_FIELD));
    Page<Notification> notifications = notificationDAO.findBySenderLikeIgnoreCase(sender, request);
    if (notifications != null)
      return notifications.getContent();
    else
      return new ArrayList<>();
  }

  @Override
  public List<Notification> findByCreatedDuration(long start, long end) {
    logger.debug(
        "NotificationHandler is finding notifications by created between " + start + " and " + end);
    return notificationDAO.findByCreatedBetween(start, end);
  }

  @Override
  public List<Notification> findByCreatedDuration(long start, long end, int limit) {
    logger.debug("NotificationHandler is finding notifications by created between " + start
        + " and " + end + IN_LIMIT + limit);
    PageRequest request =
        new PageRequest(0, limit, new Sort(Sort.Direction.DESC, RECORD_CREATION_FIELD));
    Page<Notification> notifications = notificationDAO.findByCreatedBetween(start, end, request);
    if (notifications != null)
      return notifications.getContent();
    else
      return new ArrayList<>();
  }

  @Override
  public List<Notification> findByCreatedAfter(long start) {
    logger.debug("NotificationHandler is finding notifications by created after" + start);
    return notificationDAO.findByCreatedAfter(start);
  }

  @Override
  public List<Notification> findByCreatedAfter(long start, int limit) {
    logger.debug(
        "NotificationHandler is finding notifications by created after" + start + IN_LIMIT + limit);
    PageRequest request =
        new PageRequest(0, limit, new Sort(Sort.Direction.DESC, RECORD_CREATION_FIELD));
    Page<Notification> notifications = notificationDAO.findByCreatedAfter(start, request);
    if (notifications != null)
      return notifications.getContent();
    else
      return new ArrayList<>();
  }

  @Override
  public List<Notification> findByCreatedBefore(long end) {
    logger.debug("NotificationHandler is finding notifications by created before" + end);
    return notificationDAO.findByCreatedBefore(end);
  }

  @Override
  public List<Notification> findByCreatedBefore(long end, int limit) {
    logger.debug(
        "NotificationHandler is finding notifications by created before" + end + IN_LIMIT + limit);
    PageRequest request =
        new PageRequest(0, limit, new Sort(Sort.Direction.DESC, RECORD_CREATION_FIELD));
    Page<Notification> notifications = notificationDAO.findByCreatedBefore(end, request);
    if (notifications != null)
      return notifications.getContent();
    else
      return new ArrayList<>();
  }

  @Override
  public List<Notification> findInLabels(String[] labels) {
    logger.debug("NotificationHandler is finding notifications by labels in " + labels);
    return notificationDAO.findByLabelsInIgnoreCase(labels);
  }

  @Override
  public List<Notification> findInLabels(String[] labels, int limit) {
    logger.debug(
        "NotificationHandler is finding notifications by labels in " + labels + IN_LIMIT + limit);
    PageRequest request =
        new PageRequest(0, limit, new Sort(Sort.Direction.DESC, RECORD_CREATION_FIELD));
    Page<Notification> notifications = notificationDAO.findByLabelsInIgnoreCase(labels, request);
    if (notifications != null)
      return notifications.getContent();
    else
      return new ArrayList<>();
  }

  @Override
  public List<Notification> findNewNotifications() {
    logger.debug("NotificationHandler is finding notifications by status=NEW");
    return notificationDAO.findByStatus(NotificationStatus.NEW);
  }

  @Override
  public List<Notification> findNewNotifications(int limit) {
    logger
        .debug("NotificationHandler is finding notifications by status=NEW" + " in limit=" + limit);
    PageRequest request =
        new PageRequest(0, limit, new Sort(Sort.Direction.DESC, RECORD_CREATION_FIELD));

    Page<Notification> notifications =
        notificationDAO.findByStatus(NotificationStatus.NEW, request);
    if (notifications != null)
      return notifications.getContent();
    else
      return new ArrayList<>();
  }

  @Override
  public void deleteNotificationAndTransmissionsBySlug(String slug) {
    if (slug == null) {
      logger.error("NotificationHandler received a null object");
      throw new ClientException("slug is null");
    }

    logger.debug(
        "NotificationHandler is starting deleting notification and transmissions by slug=" + slug);

    Notification notification;
    try {
      notification = notificationDAO.findBySlugIgnoreCase(slug);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ServiceException(e);
    }

    if (notification == null) {
      logger.info("the notification doesn't exist: slug=" + slug);
      throw new NotFoundException(Notification.class.toString(), slug);
    }

    try {
      logger.debug(
          "NotificationHandler is starting deleting transmissions: notification slug=" + slug);
      transmissionDAO.deleteByNotificationId(notification.getId());

      logger.debug("NotificationHandler is starting deleting the notification: slug=" + slug);
      notificationDAO.delete(notification);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ServiceException(e);
    }

    logger.debug("Deletion operation is completed: notification slug=" + slug);
  }

  @Override
  public void deleteOldProcessedNotificationsAndTransmissions(long age) {
    long end = System.currentTimeMillis() - age;
    Date endDate = new Date(end);

    logger.debug(
        "NotificationHandler is starting processed deleting notifications and transmissions by notifications modified before "
            + endDate);

    List<Notification> notifications =
        notificationDAO.findByStatusAndModifiedBefore(NotificationStatus.PROCESSED, end);

    try {
      logger.debug(
          "NotificationHandler is starting deleting transmissions by notifications modified before "
              + endDate);
      transmissionDAO.deleteByNotificationIn(notifications);

      logger.debug(
          "NotificationHandler is starting deleting notifications by notifications modified before "
              + endDate);
      notificationDAO.delete(notifications);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ServiceException(e);
    }

    logger
        .debug("Deletion operation by notifications modified before " + endDate + " is completed");
  }

  public void setNotificationDAO(NotificationDAO notificationDAO) {
    this.notificationDAO = notificationDAO;
  }

  public void setTransmissionDAO(TransmissionDAO transmissionDAO) {
    this.transmissionDAO = transmissionDAO;
  }

  public void setDistributionCoordinator(DistributionCoordinator distributionCoordinator) {
    this.distributionCoordinator = distributionCoordinator;
  }

}
