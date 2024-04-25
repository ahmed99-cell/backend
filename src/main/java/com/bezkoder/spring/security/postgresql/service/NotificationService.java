package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.Notification;

import java.util.List;

public interface NotificationService {
    Notification markNotificationAsRead(Long notificationId);
    List<Notification> getCurrentUserNotifications(String username);
}
