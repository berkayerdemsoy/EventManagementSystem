package com.example.event_service_app.kafka;

import com.example.ems_common.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Value("${kafka.topics.notification-events:notification-events}")
    private String topic;

    /**
     * recipientEmail partition key olarak kullanılır —
     * aynı alıcının mesajları her zaman aynı partition'a düşer.
     */
    public void send(NotificationEvent event) {
        kafkaTemplate.send(topic, event.getRecipientEmail(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[Kafka] Mesaj gönderilemedi. eventType={}, recipient={}, error={}",
                                event.getEventType(), event.getRecipientEmail(), ex.getMessage());
                    } else {
                        log.info("[Kafka] Mesaj gönderildi. eventType={}, recipient={}, partition={}, offset={}",
                                event.getEventType(), event.getRecipientEmail(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}

