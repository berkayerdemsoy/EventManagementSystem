package com.example.notification_service.service;

import com.example.ems_common.dto.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Dead Letter Queue consumer.
 *
 * Ana topic'ten 3 başarısız retry sonrası buraya düşen mesajları işler:
 *  - Hata detaylarını (original topic, exception, partition/offset) loglar.
 *  - MAX_DLQ_RETRIES sınırına kadar mesajı ana topic'e otomatik geri gönderir.
 *  - Sınır aşılırsa kalıcı hata olarak işaretler → manuel müdahale gerekir.
 *
 * DLQ consumer'ı kasıtlı olarak "dlqKafkaListenerContainerFactory" kullanır;
 * bu factory'de DLQ redirect yoktur — sonsuz döngü riski ortadan kalkar.
 */
@Slf4j
@Service
public class DlqConsumerService {

    /** DLQ üzerinden kaç kez ana topic'e geri gönderebiliriz. */
    private static final int MAX_DLQ_RETRIES = 1;

    /**
     * Her re-queue adımında değerini artırdığımız özel Kafka header'ı.
     * Sonsuz DLQ döngüsünü önler.
     */
    private static final String DLQ_RETRY_COUNT_HEADER = "X-DLQ-Retry-Count";

    private final KafkaTemplate<String, NotificationEvent> mainTopicKafkaTemplate;

    @Value("${kafka.topics.notification-events:notification-events}")
    private String mainTopic;

    public DlqConsumerService(
            @Qualifier("mainTopicKafkaTemplate") KafkaTemplate<String, NotificationEvent> mainTopicKafkaTemplate) {
        this.mainTopicKafkaTemplate = mainTopicKafkaTemplate;
    }

    @KafkaListener(
            topics = "${kafka.topics.notification-events-dlq:notification-events-dlq}",
            groupId = "notification-service-dlq-group",
            containerFactory = "dlqKafkaListenerContainerFactory"
    )
    public void consumeDlq(ConsumerRecord<String, NotificationEvent> record) {
        NotificationEvent event      = record.value();
        String originalTopic         = getHeaderAsString(record, KafkaHeaders.DLT_ORIGINAL_TOPIC);
        String exceptionMessage      = getHeaderAsString(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE);
        String exceptionFqcn         = getHeaderAsString(record, KafkaHeaders.DLT_EXCEPTION_FQCN);
        int retryCount               = getHeaderAsInt(record, DLQ_RETRY_COUNT_HEADER);

        log.error("""
                [DLQ] Başarısız mesaj alındı ─────────────────────────────
                  eventId        = {}
                  eventType      = {}
                  recipient      = {}
                  originalTopic  = {}
                  partition      = {}
                  offset         = {}
                  dlqRetryCount  = {}
                  exception      = {} : {}
                ──────────────────────────────────────────────────────────""",
                event != null ? event.getEventId()        : "N/A",
                event != null ? event.getEventType()      : "N/A",
                event != null ? event.getRecipientEmail() : "N/A",
                originalTopic,
                record.partition(),
                record.offset(),
                retryCount,
                exceptionFqcn, exceptionMessage);

        if (event == null) {
            log.error("[DLQ] Event deserialize edilemedi, mesaj atlanıyor. partition={} offset={}",
                    record.partition(), record.offset());
            return;
        }

        if (retryCount < MAX_DLQ_RETRIES) {
            requeue(event, retryCount);
        } else {
            log.error("[DLQ] Maksimum re-queue limiti ({}) aşıldı — kalıcı hata. " +
                            "Manuel müdahale gerekli. eventId={} recipient={}",
                    MAX_DLQ_RETRIES, event.getEventId(), event.getRecipientEmail());
            // Buraya Slack / PagerDuty / metrik entegrasyonu eklenebilir.
        }
    }

    /**
     * Mesajı ana topic'e geri gönderir.
     * Re-queue sayacını header'a ekler; bir sonraki başarısız denemede bu değer okunarak
     * sonsuz döngü önlenir.
     */
    private void requeue(NotificationEvent event, int currentRetryCount) {
        RecordHeaders headers = new RecordHeaders();
        headers.add(DLQ_RETRY_COUNT_HEADER,
                ByteBuffer.allocate(4).putInt(currentRetryCount + 1).array());

        ProducerRecord<String, NotificationEvent> producerRecord =
                new ProducerRecord<>(mainTopic, null, event.getRecipientEmail(), event, headers);

        mainTopicKafkaTemplate.send(producerRecord)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[DLQ] Mesaj ana topic'e geri gönderilemedi: eventId={}",
                                event.getEventId(), ex);
                    } else {
                        log.info("[DLQ] Mesaj ana topic'e geri gönderildi " +
                                        "(dlqRetry {}/{}): eventId={} → topic={}",
                                currentRetryCount + 1, MAX_DLQ_RETRIES,
                                event.getEventId(), mainTopic);
                    }
                });
    }

    // ─── Header yardımcıları ─────────────────────────────────────────────────

    private String getHeaderAsString(ConsumerRecord<?, ?> record, String headerName) {
        Header header = record.headers().lastHeader(headerName);
        return header != null ? new String(header.value(), StandardCharsets.UTF_8) : "unknown";
    }

    private int getHeaderAsInt(ConsumerRecord<?, ?> record, String headerName) {
        Header header = record.headers().lastHeader(headerName);
        if (header == null || header.value().length < 4) return 0;
        return ByteBuffer.wrap(header.value()).getInt();
    }
}

