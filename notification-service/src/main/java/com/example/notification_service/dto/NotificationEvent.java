package com.example.notification_service.dto;

import lombok.Data;

@Data
public class NotificationEvent {
    private Long userId;
    private String message;
    private String type; // sms , email , push
}
