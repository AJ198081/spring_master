package dev.aj.full_stack_v6.common.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final Environment environment;

    // Producer Configurations
    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // Expose KafkaOperations<Object, Object> for components that depend on the generic interface
    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaOperations<Object, Object> kafkaOperations(KafkaTemplate<String, Object> kafkaTemplate) {
        if (kafkaTemplate == null) {
            throw new IllegalStateException("KafkaTemplate must not be null");
        }

        return (KafkaOperations) kafkaTemplate;
    }

    // Supply producers to send messages to the topics
    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> kafkaBootstrapProperties =  new HashMap<>();

        kafkaBootstrapProperties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.bootstrap-servers",
                        List.class,
                        List.of("localhost:9092", "localhost:9094", "localhost:9096")
                )
        );

        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        kafkaBootstrapProperties.put(JsonSerializer.TYPE_MAPPINGS, "paymentSuccessfulEvent:dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent,OrderPlacedEvent:dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent");

        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");

        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 2);
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, 5);

        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.RETRY_BACKOFF_MS_CONFIG, Duration.ofSeconds(5).toMillis());

        // Normal JSON is only about a couple of hundred bytes
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, 2048);
        // In this case, in the worst case the producer might wait to fill up its buffer for 10 seconds
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, Duration.ofSeconds(10).toMillis());
        // Single request waiting for broker's response after sending a request
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, (int) Duration.ofSeconds(30).toMillis());
        // The maximum time a producer can keep trying to deliver a message includes time to linger, waiting for response and retrying requests
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) Duration.ofMinutes(2).toMillis());

        return new DefaultKafkaProducerFactory<>(kafkaBootstrapProperties);
    }
}
