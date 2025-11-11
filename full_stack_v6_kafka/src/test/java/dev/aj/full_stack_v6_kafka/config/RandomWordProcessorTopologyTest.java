package dev.aj.full_stack_v6_kafka.config;

import dev.aj.full_stack_v6_kafka.BootstrapTopics;
import dev.aj.full_stack_v6_kafka.TestConfig;
import dev.aj.full_stack_v6_kafka.TestDataFactory;
import dev.aj.full_stack_v6_kafka.config.admin.KafkaBootstrapProperties;
import dev.aj.full_stack_v6_kafka.config.producers.ProducerConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;


@ApplicationModuleTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES)
@Import(value = {TestConfig.class, TestDataFactory.class, BootstrapTopics.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Slf4j
@TestPropertySource(properties = {
        "junit.jupiter.execution.parallel.enabled=true"
})
//@Execution(ExecutionMode.CONCURRENT)
class RandomWordProcessorTopologyTest {

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private ProducerConfig producerConfig;

    @Autowired
    private KafkaBootstrapProperties kafkaBootstrapProperties;

    @Autowired
    private Environment environment;

    private KafkaOperations<Object, Object> producerOperation;

    @BeforeAll
    void beforeAll() {
        kafkaBootstrapProperties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        ProducerFactory<Object, Object> kafkaProducerFactory = producerConfig.producerFactory(kafkaBootstrapProperties, environment);

        producerOperation = producerConfig.kafkaTemplate(kafkaProducerFactory);
    }

    @SneakyThrows
    @RepeatedTest(value = 1, name = "{displayName} : {currentRepetition}/{totalRepetitions}")
    void testKafkaStreamsSetup() {

        Map<String, Long> wordCountByInitial = testDataFactory.getStreamOfWords()
                .limit(100L)
                .sorted(Comparator.comparing(String::toUpperCase))
                .peek(word -> producerOperation.executeInTransaction(
                        operation -> operation.send(
                                "greetings",
                                word.substring(0, 1),
                                word))
                )
                .collect(Collectors.groupingBy(
                        w -> w.substring(0, 1),
                        Collectors.counting())
                );

        log.info("wordCountByInitial : {}", wordCountByInitial);
    }
}