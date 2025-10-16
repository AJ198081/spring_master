package dev.aj.full_stack_v6_kafka.config.producers;

import dev.aj.full_stack_v6_kafka.config.admin.KafkaBootstrapProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@RequiredArgsConstructor
public class ProducerConfig {

    // Producer Configurations
    @Bean(name = "customKafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

   /**
    // * Works with the topic's 'min.insync.replicas' setting, which defines minimum replicas
    // * required for successful write acknowledgment.
    // * Applies when 'acks=all'.
    // * Rejects write if fewer than min.insync.replicas are available; to prevent data loss.
    */
    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaBootstrapProperties kafkaBootstrapProperties) {

        kafkaBootstrapProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaBootstrapProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        kafkaBootstrapProperties.put(JsonSerializer.TYPE_MAPPINGS, "paymentSuccessfulEvent:dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent,OrderPlacedEvent:dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent");


        kafkaBootstrapProperties.put(ACKS_CONFIG, "all");

        kafkaBootstrapProperties.put(ENABLE_IDEMPOTENCE_CONFIG, true);
        kafkaBootstrapProperties.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 2);

        kafkaBootstrapProperties.put(RETRIES_CONFIG, 5);
        kafkaBootstrapProperties.put(RETRY_BACKOFF_MS_CONFIG, Duration.ofSeconds(5).toMillis());

        // Normal JSON is only about a couple of hundred bytes
        kafkaBootstrapProperties.put(BATCH_SIZE_CONFIG, 2048);
        // In this case, in the worst case the producer might wait to fill up its buffer for 10 seconds
        kafkaBootstrapProperties.put(LINGER_MS_CONFIG, Duration.ofSeconds(10).toMillis());
        // Single request waiting for broker's response after sending a request
        kafkaBootstrapProperties.put(REQUEST_TIMEOUT_MS_CONFIG, (int) Duration.ofSeconds(30).toMillis());
        // The maximum time a producer can keep trying to deliver a message includes time to linger, waiting for response and retrying requests
        kafkaBootstrapProperties.put(DELIVERY_TIMEOUT_MS_CONFIG, (int) Duration.ofMinutes(2).toMillis());

        return new DefaultKafkaProducerFactory<>(kafkaBootstrapProperties);
    }
}
