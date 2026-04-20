package com.example.notification_service.service.handler;

import com.example.ems_common.dto.NotificationEvent;
import com.example.ems_common.dto.NotificationEventType;

public interface NotificationHandler {

    /**
     * Bu handler'ın hangi eventType'ı işlediğini döner.
     */
    NotificationEventType getHandledType();

    /**
     * Gelen eventi işler (mail gönderir).
     */
    void handle(NotificationEvent event);
}

