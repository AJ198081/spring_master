package dev.aj.full_stack_v6_kafka.streams;

import dev.aj.full_stack_v6_kafka.streams.custom_serdes.SerdesFactory;
import dev.aj.full_stack_v6_kafka.streams.custom_types.StringObject;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RandomWordProcessorTopology implements ApplicationListener<ContextRefreshedEvent> {

    public static final String GREETINGS_UPPERCASE_OBJECT = "greetings_uppercase_object";
    public static final String GREETINGS = "greetings";
    public static final String GREETINGS_UPPERCASE = "greetings_uppercase";
    public static final String VOWEL_SET = "aeiou";

    private final SerdesFactory serdesFactory;

    private final ApplicationContext applicationContext;

    private final Environment environment;

    private KafkaStreams kafkaStreams;
    private KafkaStreams kafkaObjectStreams;

    @Bean
    @Order(1)
    public Topology upperCaseConsonantsTopology() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamsBuilder
                .stream(GREETINGS, Consumed.with(serdesFactory.stringSerdes(), serdesFactory.stringSerdes()))
                .peek(((key, value) -> System.out.println("Greetings Consonants Topology - key: " + key + ", VALUE: " + value)))
                .filter(this::consonants)
                .mapValues((_, value) -> value.toUpperCase())
                .peek(((key, value) -> System.out.println("Consonants Topology - key: " + key + ", VALUE: " + value)))
                .to(GREETINGS_UPPERCASE, Produced.with(serdesFactory.stringSerdes(), serdesFactory.stringSerdes()));

        return streamsBuilder.build();
    }

    @Bean
    @Order(2)
    public Topology upperCaseVowelsToObjectTopology() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamsBuilder
                .stream(GREETINGS, Consumed.with(serdesFactory.stringSerdes(), serdesFactory.stringSerdes()))
                .peek(((key, value) -> System.out.println("Greetings Vowel Topology - key: " + key + ", VALUE: " + value)))
                .filter(this::vowels)
                .mapValues((_, value) -> new StringObject(value.toUpperCase()))
                .peek(((key, value) -> System.out.println("Vowel Topology - key: " + key + ", VALUE: " + value)))
                .to(GREETINGS_UPPERCASE_OBJECT, Produced.with(serdesFactory.stringSerdes(), serdesFactory.jsonSerdes()));

        return streamsBuilder.build();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("Starting Kafka Streams {}", event.getApplicationContext().getApplicationName());

        Properties streamsProperties = new Properties();
        streamsProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        streamsProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try {
            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "upper_case_consonants_topology");
            kafkaStreams = new KafkaStreams(applicationContext.getBean("upperCaseConsonantsTopology", Topology.class), streamsProperties);

            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "upper_case_vowels_to_object_topology");
            kafkaObjectStreams = new KafkaStreams(applicationContext.getBean("upperCaseVowelsToObjectTopology", Topology.class), streamsProperties);

            kafkaStreams.start();
            kafkaObjectStreams.start();
        } catch (Exception e) {
            log.error("Error starting Kafka Streams", e);
        }
        log.info("Kafka Streams started");
    }

    @PreDestroy
    public void close() {
        log.info("Closing Kafka Streams");
        kafkaStreams.close();
        kafkaObjectStreams.close();
        log.info("Kafka Streams closed");
    }

    private boolean vowels(String key, String value) {
        return getVowelSet().contains(key.toLowerCase());
    }

    private boolean consonants(String key, String value) {

        return !getVowelSet().contains(key.toLowerCase());
    }

    private static Set<String> getVowelSet() {
        return Arrays.stream(VOWEL_SET.split(""))
                .collect(Collectors.toSet());
    }
}
