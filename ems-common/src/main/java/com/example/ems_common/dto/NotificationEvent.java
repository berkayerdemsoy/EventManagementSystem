package com.example.ems_common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {

    @Builder.Default
    private String eventId = UUID.randomUUID().toString();

    private NotificationEventType eventType;

    private String recipientEmail;

    @Builder.Default
    private Map<String, String> payload = new HashMap<>();
}

