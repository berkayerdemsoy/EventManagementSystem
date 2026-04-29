package com.example.notification_service.config;

import com.example.ems_common.dto.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.notification-events-dlq:notification-events-dlq}")
    private String dlqTopic;

    /**
     * DLQ'ya mesaj göndermek için ayrı bir KafkaTemplate.
     * Consumer'ın kendi template'ini kullanmak döngüsel bağımlılık yaratabilir.
     */
    @Bean
    public KafkaTemplate<String, NotificationEvent> dlqKafkaTemplate() {
        return new KafkaTemplate<>(buildProducerFactory());
    }

    /**
     * DLQ'dan ana topic'e re-queue yapmak için kullanılan KafkaTemplate.
     * DlqConsumerService tarafından @Qualifier("mainTopicKafkaTemplate") ile inject edilir.
     */
    @Bean
    public KafkaTemplate<String, NotificationEvent> mainTopicKafkaTemplate() {
        return new KafkaTemplate<>(buildProducerFactory());
    }

    /** Her iki producer template için ortak ProducerFactory. */
    private DefaultKafkaProducerFactory<String, NotificationEvent> buildProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory,
            KafkaTemplate<String, NotificationEvent> dlqKafkaTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);

        // Hata alınan mesajı DLQ topic'ine yönlendir
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dlqKafkaTemplate,
                (record, ex) -> new org.apache.kafka.common.TopicPartition(dlqTopic, 0)
        );

        // 3 retry, 2 saniye aralık
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 3));

        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(3);

        return factory;
    }

    /**
     * DLQ consumer'ına özel factory — DLQ redirect YOKTUR.
     *
     * Ana topic factory'si DLQ'ya hata gönderir; bu factory aynı şeyi yapsaydı
     * başarısız DLQ mesajları tekrar DLQ'ya yazılır → sonsuz döngü oluşurdu.
     * Bunun yerine: 0 retry, sadece logla ve geç.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> dlqKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, consumerFactory);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, ex) -> log.error(
                        "[DLQ-Factory] DLQ mesajı işlenirken kritik hata — atlanıyor. " +
                        "partition={} offset={} exception={}",
                        record.partition(), record.offset(), ex.getMessage()),
                new FixedBackOff(0L, 0)   // retry yok, direkt recoverer'a git
        );
        factory.setCommonErrorHandler(errorHandler);
        factory.setConcurrency(1);

        return factory;
    }
}
