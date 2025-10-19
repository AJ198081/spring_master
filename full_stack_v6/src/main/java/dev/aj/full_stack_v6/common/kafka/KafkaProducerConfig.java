package dev.aj.full_stack_v6.common.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jspecify.annotations.NonNull;
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

import static org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION;
import static org.apache.kafka.clients.producer.ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.RETRY_BACKOFF_MS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final Environment environment;

     /**
     * Configures and provides a {@link KafkaOperations} bean, which serves as a general producer to send messages to Kafka.
     * An abstraction that Spring Modulith uses to send messages, to interact with Kafka in a type-independent manner.
     *
     * @param producerFactory the {@link ProducerFactory} that has configuration for the KafkaTemplate (producer); must not be null.
     * @return an instance of {@link KafkaOperations}, {@link KafkaTemplate} being the concrete implementation, configured using the provided {@link ProducerFactory}.
     */
    @Bean
    @Primary
    public KafkaTemplate<Object, Object> kafkaTemplate(ProducerFactory<Object, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // Supply producers to send messages to the topics
    @Bean
    public @NonNull ProducerFactory<Object, Object> producerFactory() {

        Map<String, Object> kafkaBootstrapProperties = new HashMap<>();

        kafkaBootstrapProperties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.bootstrap-servers",
                        List.class,
                        List.of("localhost:9092", "localhost:9094", "localhost:9096")
                )
        );

        kafkaBootstrapProperties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaBootstrapProperties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        kafkaBootstrapProperties.put(JsonSerializer.TYPE_MAPPINGS, "paymentSuccessfulEvent:dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent,orderPlacedEvent:dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent");

//     "all" means that the producer will wait for all the 'min-insync-replicas', which is set during topic creation.
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
