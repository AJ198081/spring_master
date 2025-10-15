package dev.aj.full_stack_v6_kafka.config.consumers;

import dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent;
import dev.aj.full_stack_v6_kafka.common.exceptions.NotRetryableException;
import dev.aj.full_stack_v6_kafka.common.exceptions.RetryableException;
import dev.aj.full_stack_v6_kafka.config.admin.KafkaBootstrapProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ConsumerConfig {

    // Supply consumers to listen to the topics
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessfulEvent> kafkaListenerContainerFactory(ConsumerFactory<String, PaymentSuccessfulEvent> consumerFactory,
                                                                                                                 KafkaTemplate<String, Object> kafkaTemplate) {
        // Mechanism to handle exceptions whilst consuming messages by the listener
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, new FixedBackOff(1000, 2));
        // Enable forwarding of any deserialization errors to the DLQ
        defaultErrorHandler.setAckAfterHandle(true); // Disable committing of offset to retry the same record
        defaultErrorHandler.addNotRetryableExceptions(NotRetryableException.class);
        defaultErrorHandler.addRetryableExceptions(RetryableException.class);

        ConcurrentKafkaListenerContainerFactory<String, PaymentSuccessfulEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setAutoStartup(true);
        factory.setCommonErrorHandler(defaultErrorHandler);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentSuccessfulEvent> consumerFactory(KafkaBootstrapProperties kafkaBootstrapProperties) {

        kafkaBootstrapProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

//        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, objectDeserializer);
//        objectDeserializer.addTrustedPackages("dev.aj.kafka");

        // Error handling deserializer - a wrapper around the actual deserializer
        // will handle any serialization exceptions
        kafkaBootstrapProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        kafkaBootstrapProperties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        kafkaBootstrapProperties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        kafkaBootstrapProperties.put(JsonDeserializer.TRUSTED_PACKAGES, "dev.aj.full_stack_v6.common.domain.events,dev.aj.kafka.product.domain.entities");
        kafkaBootstrapProperties.put(JsonDeserializer.TYPE_MAPPINGS, "paymentSuccessfulEvent:dev.aj.full_stack_v6.common.domain.events.PaymentSuccessfulEvent");


        kafkaBootstrapProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "productCreatedDto-consumer-group");
        kafkaBootstrapProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        // The best way to optimize your Kafka cluster in general is to flash it up with minimum configuration,
        // Determine from the log what you really need
        kafkaBootstrapProperties.put(org.apache.kafka.clients.consumer.ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        return new DefaultKafkaConsumerFactory<>(kafkaBootstrapProperties);
    }

}
