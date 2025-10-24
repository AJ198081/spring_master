package dev.aj.full_stack_v6_kafka.config.consumers;

import dev.aj.full_stack_v6_kafka.common.exceptions.NotRetryableException;
import dev.aj.full_stack_v6_kafka.common.exceptions.RetryableException;
import dev.aj.full_stack_v6_kafka.config.admin.KafkaBootstrapProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Configuration
@RequiredArgsConstructor
public class ConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(ConsumerFactory<String, Object> consumerFactory,
                                                                                                 KafkaTemplate<String, java.lang.Object> kafkaTemplate) {
        // Mechanism to handle exceptions whilst consuming messages by the listener, generally due to deserialization exceptions
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        // Enable forwarding of any deserialization errors to the DLQ
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, new FixedBackOff(30000, 5));

        // Enable or disable committing of offset to retry the same record
        defaultErrorHandler.setAckAfterHandle(true);

        defaultErrorHandler.addNotRetryableExceptions(NotRetryableException.class, HttpServerErrorException.class);
        // Add any other exceptions that you want to retry, ideally if a connecting service isn't available, e.g., status is say 'unavailable'
        defaultErrorHandler.addRetryableExceptions(RetryableException.class, ResourceAccessException.class);

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAutoStartup(true);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

    /**
     * The best way to optimize your Kafka cluster is to flash it up with minimum configuration and then
     * determine from the log what you really need
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaBootstrapProperties kafkaBootstrapProperties, Environment environment) {

        kafkaBootstrapProperties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaBootstrapProperties.put(VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        // Error handling deserializer - a wrapper around the actual deserializer
        // will handle any serialization exceptions and ensure those are transferred to DLT
        kafkaBootstrapProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        kafkaBootstrapProperties.put(GROUP_ID_CONFIG, "kafka_consumer_group");
        kafkaBootstrapProperties.put(AUTO_OFFSET_RESET_CONFIG, "latest");
        kafkaBootstrapProperties.put(PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        kafkaBootstrapProperties.put(JsonDeserializer.TYPE_MAPPINGS, """
                paymentSuccessfulEvent:dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent,
                orderPlacedEvent:dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent,
                withdrawalEvent:dev.aj.full_stack_v6_kafka.common.domain.events.WithdrawalEvent,
                depositEvent:dev.aj.full_stack_v6_kafka.common.domain.events.DepositEvent
                """);

        kafkaBootstrapProperties.put(JsonDeserializer.TRUSTED_PACKAGES, """
                dev.aj.full_stack_v6.common.domain.events,
                dev.aj.full_stack_v6_kafka.common.domain.events
                """);

//        kafkaBootstrapProperties.put(ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.name());
        kafkaBootstrapProperties.put(
                ISOLATION_LEVEL_CONFIG,
                environment.getProperty("spring.kafka.consumer.isolation-level", String.class, IsolationLevel.READ_COMMITTED.name())
                        .toLowerCase()
        );

        return new DefaultKafkaConsumerFactory<>(kafkaBootstrapProperties);
    }

}
