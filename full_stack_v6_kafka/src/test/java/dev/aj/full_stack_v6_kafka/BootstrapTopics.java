package dev.aj.full_stack_v6_kafka;

import dev.aj.full_stack_v6_kafka.config.admin.AdminConfiguration;
import dev.aj.full_stack_v6_kafka.streams.RandomWordProcessorTopology;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.core.env.Environment;

import java.util.Map;

@TestComponent
@RequiredArgsConstructor
@Slf4j
public class BootstrapTopics {

    public static final String PAYMENTS_TOPIC = "payments";

    private final AdminConfiguration adminConfiguration;
    private final Environment environment;

    @PostConstruct
    public void createTopics() {
        Map<String, String> commonTopicConfigurations = Map.of("min.insync.replicas", "3");

        adminConfiguration.createTopic(environment.getProperty("kafka.topics.orders"), commonTopicConfigurations);
        adminConfiguration.createTopic(PAYMENTS_TOPIC, commonTopicConfigurations);

        adminConfiguration.createTopic(environment.getProperty("kafka.topics.deposits"), commonTopicConfigurations);
        adminConfiguration.createTopic(environment.getProperty("kafka.topics.withdrawals"), commonTopicConfigurations);

        adminConfiguration.createTopic(RandomWordProcessorTopology.GREETINGS, commonTopicConfigurations);
        adminConfiguration.createTopic(RandomWordProcessorTopology.GREETINGS_UPPERCASE, commonTopicConfigurations);
        adminConfiguration.createTopic(RandomWordProcessorTopology.GREETINGS_UPPERCASE_OBJECT, commonTopicConfigurations);
        adminConfiguration.createTopic(RandomWordProcessorTopology.WORDS_WITH_4_OR_MORE_LETTERS, commonTopicConfigurations);
        adminConfiguration.createTopic(RandomWordProcessorTopology.WORDS_WITH_LESS_THAN_4_LETTERS, commonTopicConfigurations);
    }

}
