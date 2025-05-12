package dev.aj.kafka.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
@RequiredArgsConstructor
public class KafkaConfig {

    private final Environment environment;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.bootstrap-servers",
                        List.class,
                        List.of("localhost:9092", "localhost:9094", "localhost:9096"))
        ));
    }

    public NewTopic createTopic(String topicName) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .configs(Map.of(
                        "min.insync.replicas", "2",
                        "cleanup.policy", "compact")
                )
                .build();
    }

    @Bean(name = "customKafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> producerConfigs = new HashMap<>();
        producerConfigs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.bootstrap-servers",
                        List.class,
                        List.of("localhost:9092", "localhost:9094", "localhost:9096"))
        );
        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        producerConfigs.put(ProducerConfig.ACKS_CONFIG, "all");

        producerConfigs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        producerConfigs.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        producerConfigs.put(ProducerConfig.RETRIES_CONFIG, 5);
        producerConfigs.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, Duration.ofSeconds(5).toMillis());

        producerConfigs.put(ProducerConfig.BATCH_SIZE_CONFIG, 512); // Normal JSON is only about a couple of hundred bites
        producerConfigs.put(ProducerConfig.LINGER_MS_CONFIG, Duration.ofSeconds(10).toMillis()); // In this case you will end up waiting for 10 seconds
        producerConfigs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, (int) Duration.ofSeconds(30).toMillis());// Single request waiting for broker's response after sending a request
        // Maximum time producer can spend trying to deliver a message includes time to linger, waiting for response and retrying requests
        producerConfigs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) Duration.ofMinutes(2).toMillis());

        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }


    public Map<String, Object> getKafkaProperties() {
        return Map.of(
                "bootstrap.servers",
                environment.getProperty("spring.kafka.bootstrap-servers",
                        List.class,
                        List.of("localhost:9092", "localhost:9094", "localhost:9096"))
        );
    }

}
