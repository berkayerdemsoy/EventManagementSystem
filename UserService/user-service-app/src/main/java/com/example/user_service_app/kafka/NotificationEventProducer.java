package com.example.user_service_app.kafka;

import com.example.ems_common.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

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

    /**
     * Outbox Relay için senkron gönderim.
     * Kafka broker'dan dönen onay (SendResult) alınana kadar bloklar;
     * gönderim başarısız olursa RuntimeException fırlatır.
     */
    public SendResult<String, NotificationEvent> sendSync(NotificationEvent event) {
        ProducerRecord<String, NotificationEvent> record = new ProducerRecord<>(
                topic,
                event.getRecipientEmail(),
                event
        );
        try {
            SendResult<String, NotificationEvent> result = kafkaTemplate.send(record).get();
            log.info("[Kafka] Senkron mesaj gönderildi. eventType={}, recipient={}, partition={}, offset={}",
                    event.getEventType(), event.getRecipientEmail(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka senkron gönderim kesintiye uğradı. eventType=" + event.getEventType(), e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("[Kafka] Senkron mesaj gönderilemedi. eventType={}, recipient={}, error={}",
                    event.getEventType(), event.getRecipientEmail(), cause.getMessage());
            throw new RuntimeException("Kafka senkron gönderim başarısız. eventType=" + event.getEventType(), cause);
        }
    }
}

