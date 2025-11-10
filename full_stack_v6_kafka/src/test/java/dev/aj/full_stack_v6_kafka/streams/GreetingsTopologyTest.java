package dev.aj.full_stack_v6_kafka.streams;

import dev.aj.full_stack_v6_kafka.BootstrapTopics;
import dev.aj.full_stack_v6_kafka.TestConfig;
import dev.aj.full_stack_v6_kafka.TestDataFactory;
import dev.aj.full_stack_v6_kafka.config.admin.KafkaBootstrapProperties;
import dev.aj.full_stack_v6_kafka.config.producers.ProducerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(value = {TestConfig.class, TestDataFactory.class, BootstrapTopics.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
@TestPropertySource(properties = {
        "junit.jupiter.execution.parallel.enabled=true"
})
@Execution(ExecutionMode.CONCURRENT)
class GreetingsTopologyTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private GreetingsTopology greetingsTopology;

    @Autowired
    private ProducerConfig producerConfig;

    @Autowired
    private KafkaBootstrapProperties kafkaBootstrapProperties;

    @Autowired
    private Environment environment;

    private KafkaOperations<Object, Object> producerOperation;

    private KafkaStreams kafkaStreams;

    @BeforeAll
    void beforeAll() {

        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        ProducerFactory<Object, Object> kafkaProducerFactory = producerConfig.producerFactory(kafkaBootstrapProperties, environment);

        producerOperation = producerConfig.kafkaTemplate(kafkaProducerFactory);

        Properties streamsProperties = new Properties();
        streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "greetings-app");
        streamsProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        streamsProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      /*  try (KafkaStreams greetingsStream = new KafkaStreams(greetingsTopology.upperCaseTopology(), streamsProperties)) {
            greetingsStream.start();
            log.info("Kafka Streams started");
            Thread.sleep(10000L);
        } catch (Exception e) {
            log.error("Error starting Kafka Streams", e);
        }*/

        try {
            kafkaStreams = new KafkaStreams(greetingsTopology.upperCaseTopology(), streamsProperties);
            kafkaStreams.start();
        } catch (Exception e) {
            log.error("Error starting Kafka Streams", e);
        }
    }

    @AfterAll
    void afterAll() {
        kafkaStreams.close();
    }

    @Test
    void testKafkaStreamsSetup() {

        Map<String, Long> wordCountByInitial = testDataFactory.getStreamOfWords()
                .limit(100L)
                .sorted(Comparator.comparing(String::toUpperCase))
                .peek(word -> producerOperation.executeInTransaction(
                                operation -> operation.send(
                                        GreetingsTopology.GREETINGS,
                                        word.substring(0, 1),
                                        word)
                        )
                )
                .collect(Collectors.groupingBy(w -> w.substring(0, 1), Collectors.counting()));

        log.info("wordCountByInitial : {}", wordCountByInitial);
    }
}