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

@Configuration
@RequiredArgsConstructor
public class ProducerConfig {

    // Producer Configurations
    @Bean(name = "customKafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // Supply producers to send messages to the topics
    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaBootstrapProperties kafkaBootstrapProperties) {

        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        kafkaBootstrapProperties.put(JsonSerializer.TYPE_MAPPINGS, "productCreatedDto:dev.aj.kafka.product.domain.dto.ProductCreatedDto");

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
