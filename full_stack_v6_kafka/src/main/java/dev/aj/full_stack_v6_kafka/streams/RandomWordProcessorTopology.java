package dev.aj.full_stack_v6_kafka.streams;

import dev.aj.full_stack_v6_kafka.streams.custom_serdes.SerdesFactory;
import dev.aj.full_stack_v6_kafka.streams.custom_types.StringObject;
import dev.aj.full_stack_v6_kafka.streams.exception_handlers.StreamsDeserializationExceptionHandler;
import dev.aj.full_stack_v6_kafka.streams.exception_handlers.StreamsProcessorExceptionHandler;
import dev.aj.full_stack_v6_kafka.streams.exception_handlers.StreamsSerializationExceptionHandler;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Named;
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
    public static final String WORDS_WITH_4_OR_MORE_LETTERS = "words_with_4_or_more_letters";
    public static final String WORDS_WITH_LESS_THAN_4_LETTERS = "words_with_less_than_4_letters";
    public static final String VOWEL_SET = "aeiou";

    private final SerdesFactory serdesFactory;

    private final ApplicationContext applicationContext;

    private final Environment environment;

    private KafkaStreams kafkaStreams;
    private KafkaStreams kafkaObjectStreams;
    private KafkaStreams branchedKafkaStreams;

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
                .peek(((key, value) -> System.out.println("Greeting Vowel Topology - key: " + key + ", VALUE: " + value)))
                .filter(this::vowels)
                .mapValues((_, value) -> new StringObject(value.toUpperCase()))
                .peek(((key, value) -> System.out.println("Vowel Topology - key: " + key + ", VALUE: " + value)))
                .to(GREETINGS_UPPERCASE_OBJECT, Produced.with(serdesFactory.stringSerdes(), serdesFactory.jsonSerdes()));

        return streamsBuilder.build();
    }

    @Bean
    public Topology allInclusiveWordProcessorTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamsBuilder.stream(GREETINGS, Consumed.with(Serdes.String(), Serdes.String()))
                .peek(((key, value) -> System.out.println("All-inclusive Topology - key: " + key + ", VALUE: " + value)))
                .split(Named.as("all_inclusive_topology"))

                .branch(this::valueHas4OrMoreLetters,
                        Branched
                                .withConsumer(wordWith4OrMoreLetters -> wordWith4OrMoreLetters
                                        .to(WORDS_WITH_4_OR_MORE_LETTERS, Produced.with(Serdes.String(), Serdes.String()))))

                .branch(this::valueHasLessThan4Letters,
                        Branched
                                .withConsumer(wordsWithLessThan4Letters -> wordsWithLessThan4Letters
                                        .to(WORDS_WITH_LESS_THAN_4_LETTERS, Produced.with(Serdes.String(), Serdes.String()))));

        return streamsBuilder.build();
    }


    /**
     * Two ways to achieve parallelism in Kafka Streams:
     * <br><br><b>1. Stream Thread Level Parallelism:</b>
     * <li> Set the number of stream threads equal to the number of topic partitions</li>
     * <li> Each thread processes a subset of partitions</li>
     * <li> Configured via NUM_STREAM_THREADS_CONFIG property</li>
     * <br><br<b>2. Application Instance Level Parallelism:</b>
     * <li> Deploy multiple instances of the same Streams application</li>
     * <li> All instances must use the same application.id</li>
     * <li>So they belong to the same consumer group. Behind the scene Kafka Stream is just a Topology/pipeline of Producers and Consumers</li>
     * <li> Maximum useful instances = number of topic partitions</li>
     * <li> Partitions are automatically distributed across instances</li>
     * <li> Additional instances beyond partition count will remain idle</li>
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("Starting Kafka Streams {}", event.getApplicationContext().getApplicationName());

        Properties streamsProperties = new Properties();

        streamsProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        streamsProperties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "3");

        streamsProperties.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, StreamsDeserializationExceptionHandler.class);
        streamsProperties.put(StreamsConfig.PROCESSING_EXCEPTION_HANDLER_CLASS_CONFIG, StreamsProcessorExceptionHandler.class);
        streamsProperties.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, StreamsSerializationExceptionHandler.class);

        streamsProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try {
            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "upper_case_consonants_topology");
            kafkaStreams = new KafkaStreams(applicationContext.getBean("upperCaseConsonantsTopology", Topology.class), streamsProperties);

            // Alternative way to set the Stream Processor Exception Handler, per stream
            kafkaStreams.setUncaughtExceptionHandler(new StreamsProcessorExceptionHandler());

            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "upper_case_vowels_to_object_topology");
            kafkaObjectStreams = new KafkaStreams(applicationContext.getBean("upperCaseVowelsToObjectTopology", Topology.class), streamsProperties);

            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "all_inclusive_topology");
            branchedKafkaStreams = new KafkaStreams(applicationContext.getBean("allInclusiveWordProcessorTopology", Topology.class), streamsProperties);

            kafkaStreams.start();
            kafkaObjectStreams.start();
            branchedKafkaStreams.start();
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
        branchedKafkaStreams.close();
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

    private boolean valueHas4OrMoreLetters(String key, String value) {
        return value.length() >= 4;
    }

    private boolean valueHasLessThan4Letters(String key, String value) {
        return value.length() < 4;
    }
}
