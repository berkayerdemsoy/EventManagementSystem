package com.example.notification_service.service;

import com.example.notification_service.dto.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    @KafkaListener(topics = "notification-events", groupId = "notification-service-group")
    public void consume(NotificationEvent  notificationEvent) {
        System.out.println("Yeni bildirim talebi alındı!");
        System.out.println("Kullanıcı: " + notificationEvent.getUserId());
        System.out.println("Mesaj: " + notificationEvent.getMessage());
    }
}
