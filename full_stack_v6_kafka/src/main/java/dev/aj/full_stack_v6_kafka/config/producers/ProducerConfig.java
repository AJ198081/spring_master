package dev.aj.full_stack_v6_kafka.config.producers;

import dev.aj.full_stack_v6_kafka.config.admin.KafkaBootstrapProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.time.Duration;

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION;
import static org.apache.kafka.clients.producer.ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRY_BACKOFF_MAX_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRY_BACKOFF_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.TRANSACTIONAL_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Producer is needed for publishing any message to Kafka, including to Dead Letter Queue (DLQ).
 */
@Configuration
@RequiredArgsConstructor
public class ProducerConfig {

    @Bean
    @Primary
    public KafkaOperations<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Works with the topic's 'min.insync.replicas' setting, which defines minimum replicas
     * required for successful write acknowledgment.
     * Applies when 'acks=all'.
     * Rejects write if fewer than min.insync.replicas are available; to prevent data loss.
     */
    @Bean
    @Primary
    public ProducerFactory<Object, Object> producerFactory(KafkaBootstrapProperties kafkaBootstrapProperties, Environment environment) {

        kafkaBootstrapProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaBootstrapProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        kafkaBootstrapProperties.put(JsonSerializer.TYPE_MAPPINGS, """
                         withdrawalEvent:dev.aj.full_stack_v6_kafka.common.domain.events.WithdrawalEvent,
                         depositEvent:dev.aj.full_stack_v6_kafka.common.domain.events.DepositEvent
                """);

        kafkaBootstrapProperties.put(ACKS_CONFIG, "all");

        kafkaBootstrapProperties.put(ENABLE_IDEMPOTENCE_CONFIG, true);
        kafkaBootstrapProperties.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 2);

        kafkaBootstrapProperties.put(RETRIES_CONFIG, 5);
        kafkaBootstrapProperties.put(RETRY_BACKOFF_MAX_MS_CONFIG, Duration.ofSeconds(5).toMillis());
        kafkaBootstrapProperties.put(RETRY_BACKOFF_MS_CONFIG, Duration.ofSeconds(5).toMillis());

        // Normal JSON is only about a couple of hundred bytes
        kafkaBootstrapProperties.put(BATCH_SIZE_CONFIG, 2048);
        // In this case, in the worst case the producer might wait to fill up its buffer for 10 seconds
        kafkaBootstrapProperties.put(LINGER_MS_CONFIG, Duration.ofSeconds(10).toMillis());
        // Single request waiting for broker's response after sending a request
        kafkaBootstrapProperties.put(REQUEST_TIMEOUT_MS_CONFIG, (int) Duration.ofSeconds(30).toMillis());
        // The maximum time a producer can keep trying to deliver a message includes time to linger, waiting for response and retrying requests
        kafkaBootstrapProperties.put(DELIVERY_TIMEOUT_MS_CONFIG, (int) Duration.ofMinutes(2).toMillis());

        // Required to enable transactional kafka producer, as we are manually configuring the producer factory
        kafkaBootstrapProperties.put(TRANSACTIONAL_ID_CONFIG, environment.getRequiredProperty("spring.kafka.producer.transaction-id-prefix"));

        return new DefaultKafkaProducerFactory<>(kafkaBootstrapProperties);
    }
}
