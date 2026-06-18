package com.example.event_service_app.scheduler;

import com.example.ems_common.dto.NotificationEvent;
import com.example.event_service_app.entity.OutboxEvent;
import com.example.event_service_app.kafka.NotificationEventProducer;
import com.example.event_service_app.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxEventRepository outboxEventRepository;
    private final NotificationEventProducer notificationEventProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void relay() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("[OutboxRelay] {} adet işlenmemiş outbox kaydı bulundu.", pendingEvents.size());

        for (OutboxEvent outboxEvent : pendingEvents) {
            try {
                NotificationEvent notificationEvent = objectMapper.readValue(
                        outboxEvent.getPayload(),
                        NotificationEvent.class
                );

                notificationEventProducer.sendSync(notificationEvent);

                outboxEvent.setProcessed(true);
                log.info("[OutboxRelay] Outbox kaydı Kafka'ya gönderildi ve işaretlendi. id={}, eventType={}",
                        outboxEvent.getId(), outboxEvent.getEventType());
            } catch (Exception e) {
                log.error("[OutboxRelay] Outbox kaydı gönderilemedi, sonraki tick'te tekrar denenecek. id={}, eventType={}",
                        outboxEvent.getId(), outboxEvent.getEventType(), e);
            }
        }
    }
}
