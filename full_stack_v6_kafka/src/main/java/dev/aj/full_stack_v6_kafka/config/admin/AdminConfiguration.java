package dev.aj.full_stack_v6_kafka.config.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminConfiguration {

    private final Environment environment;
    private final ApplicationContext applicationContext;

    @Bean
    public KafkaAdmin kafkaAdmin(Map<String, Object> kafkaBootstrapProperties) {
        return new KafkaAdmin(kafkaBootstrapProperties);
    }

    private boolean topicExists(String topicName) {
        try (AdminClient adminClient = AdminClient.create(getKafkaProperties())) {
            return adminClient.listTopics()
                    .names()
                    .get()
                    .contains(topicName);
        } catch (Exception e) {
            return false;
        }
    }

    public NewTopic createTopic(String topicName, Map<String, String> topicConfig) {

        if (topicExists(topicName)) {
            return null;
        }

        NewTopic configuredTopic = TopicBuilder.name(topicName)
                .partitions(3)
                .replicas(3)
                .configs(topicConfig)
                .build();

        log.info("Creating topic: {}", configuredTopic.name());

        KafkaAdmin kafkaAdmin = applicationContext.getBean("kafkaAdmin", KafkaAdmin.class);
        kafkaAdmin.createOrModifyTopics(configuredTopic);

        log.info("Created topic: {}", configuredTopic.name());

        return configuredTopic;
    }

    @Bean(name = "kafkaBootstrapProperties")
    public Map<String, Object> getKafkaProperties() {

        Map<String, Object> bootstrapServerProperties = new HashMap<>();

        bootstrapServerProperties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getProperty("spring.kafka.bootstrap-servers",
                        List.class,
                        List.of("localhost:9092", "localhost:9094", "localhost:9096")
                )
        );

        // Can't in-line as the downstream consumers need modifiable Map to add additional properties
        return bootstrapServerProperties;
    }
}
