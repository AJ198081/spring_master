package dev.aj.reactivekafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final Environment environment;

    public Map<String, Object> getConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers", String.class, "localhost:9092"),
                ConsumerConfig.GROUP_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-id", String.class, "reactive-kafka-test-group"),
                ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, environment.getProperty("spring.kafka.consumer.group-instance-id", String.class, "reactive-kafka-test-instance-1"),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, environment.getProperty(
                        "spring.kafka.consumer.auto-offset-reset",
                        String.class,
                        "earliest")
        );
    }
}
