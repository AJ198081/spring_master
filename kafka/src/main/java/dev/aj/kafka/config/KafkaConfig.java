package dev.aj.kafka.config;

import dev.aj.kafka.email.exceptions.NotRetryableException;
import dev.aj.kafka.email.exceptions.RetryableException;
import dev.aj.kafka.product.domain.dto.ProductCreatedDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {

    private final Environment environment;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.bootstrap-servers",
                        List.class,
                        List.of("localhost:9092", "localhost:9094", "localhost:9096")
                )
        ));
    }

    public NewTopic createTopic(String topicName, Map<String, String> topicConfig) {
        return TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .configs(topicConfig)
                .build();
    }

    //    Producer Configurations
    @Bean(name = "customKafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    //    Kafka Producer Factory - This will supply the producers to send messages to the topics
    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> producerConfigs = getKafkaProperties();

        producerConfigs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerConfigs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        producerConfigs.put(JsonSerializer.TYPE_MAPPINGS, "productCreatedDto:dev.aj.kafka.product.domain.dto.ProductCreatedDto");

        producerConfigs.put(ProducerConfig.ACKS_CONFIG, "all");

        producerConfigs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        producerConfigs.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 2);
        producerConfigs.put(ProducerConfig.RETRIES_CONFIG, 5);

        producerConfigs.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, Duration.ofSeconds(5).toMillis());

        // Normal JSON is only about a couple of hundred bytes
        producerConfigs.put(ProducerConfig.BATCH_SIZE_CONFIG, 2048);
        // In this case you will end up waiting for 10 seconds
        producerConfigs.put(ProducerConfig.LINGER_MS_CONFIG, Duration.ofSeconds(10).toMillis());
        // Single request waiting for broker's response after sending a request
        producerConfigs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, (int) Duration.ofSeconds(30).toMillis());
        // Maximum time producer can spend trying to deliver a message includes time to linger, waiting for response and retrying requests
        producerConfigs.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) Duration.ofMinutes(2).toMillis());

        return new DefaultKafkaProducerFactory<>(producerConfigs);
    }

    // Consumer Configurations
    // Kafka Consumer Factory - This will supply the consumers to listen to the topics
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductCreatedDto> kafkaListenerContainerFactory(ConsumerFactory<String, ProductCreatedDto> consumerFactory,
                                                                                                            KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, ProductCreatedDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAutoStartup(true);

        // Mechanism to handle exceptions whilst consuming messages by the listener
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, new FixedBackOff(1000, 2));

        // Enable forwarding of any deserialization errors to the DLQ
        defaultErrorHandler.setAckAfterHandle(true); // Disable committing of offset to retry the same record

        defaultErrorHandler.addNotRetryableExceptions(NotRetryableException.class);

        defaultErrorHandler.addRetryableExceptions(RetryableException.class);


        factory.setCommonErrorHandler(defaultErrorHandler);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, ProductCreatedDto> consumerFactory() {

        Map<String, Object> consumerConfig = getKafkaProperties();

        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

//        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, objectDeserializer);
//        objectDeserializer.addTrustedPackages("dev.aj.kafka");

        // Error handling deserializer - a wrapper around the actual deserializer
        // will handle any serialization exceptions
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        consumerConfig.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        consumerConfig.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        consumerConfig.put(JsonDeserializer.TRUSTED_PACKAGES, "dev.aj.kafka");
        consumerConfig.put(JsonDeserializer.TYPE_MAPPINGS, "productCreatedDto:dev.aj.kafka.product.domain.dto.ProductCreatedDto");


        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "productCreatedDto-consumer-group");
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // The best way to optimize your Kafka cluster in general is to flash it up with minimum configuration,
        // Determine from the log what you really need
        consumerConfig.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        return new DefaultKafkaConsumerFactory<>(consumerConfig);
    }

    public Map<String, Object> getKafkaProperties() {

        Map<String, Object> bootstrapProperties = new HashMap<>();

        // Kafka bootstrap servers
        String bootstrapServers = environment.getProperty("spring.kafka.bootstrap-servers",
                "localhost:9092,localhost:9094,localhost:9096");

        bootstrapProperties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        return bootstrapProperties;
    }

}
