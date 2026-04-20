package com.example.notification_service.service;

import com.example.ems_common.dto.NotificationEvent;
import com.example.ems_common.dto.NotificationEventType;
import com.example.notification_service.service.handler.NotificationHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final List<NotificationHandler> handlers;

    // eventType → handler lookup map'i — uygulama başlangıcında doldurulur
    private final Map<NotificationEventType, NotificationHandler> handlerMap =
            new EnumMap<>(NotificationEventType.class);

    @PostConstruct
    void initHandlerMap() {
        handlers.forEach(h -> handlerMap.put(h.getHandledType(), h));
        log.info("[NotificationConsumer] Kayıtlı handler'lar: {}", handlerMap.keySet());
    }

    @KafkaListener(
            topics = "${kafka.topics.notification-events:notification-events}",
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(NotificationEvent event) {
        log.info("[NotificationConsumer] Event alındı: eventId={}, type={}, recipient={}",
                event.getEventId(), event.getEventType(), event.getRecipientEmail());

        NotificationHandler handler = handlerMap.get(event.getEventType());

        if (handler == null) {
            log.warn("[NotificationConsumer] İşlenemeyen eventType: {}", event.getEventType());
            return;
        }

        handler.handle(event);
    }
}
