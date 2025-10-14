package dev.aj.full_stack_v6_kafka;

import dev.aj.full_stack_v6_kafka.config.admin.AdminConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestComponent;

import java.util.Map;

@TestComponent
@RequiredArgsConstructor
@Slf4j
public class BootstrapTopics {

    public static final String ORDERS_TOPIC = "orders";
    public static final String PAYMENTS_TOPIC = "payments";

    private final AdminConfiguration adminConfiguration;

    @PostConstruct
    public void createTopics() {

        Map<String, String> commonTopicConfigurations = Map.of("min.insync.replicas", "2");

        NewTopic ordersTopic = adminConfiguration.createTopic(ORDERS_TOPIC, commonTopicConfigurations);
        if (ordersTopic == null) {
            log.info("Topic {} already exists.", ORDERS_TOPIC);
        }

        NewTopic paymentsTopic = adminConfiguration.createTopic(PAYMENTS_TOPIC, commonTopicConfigurations);
        if (paymentsTopic == null) {
            log.info("Topic {} already exists.", PAYMENTS_TOPIC);
        }
    }

}
