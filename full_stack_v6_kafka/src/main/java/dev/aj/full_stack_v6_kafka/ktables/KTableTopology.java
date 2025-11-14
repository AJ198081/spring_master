package dev.aj.full_stack_v6_kafka.ktables;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Slf4j
@RequiredArgsConstructor
public class KTableTopology implements ApplicationListener<ContextRefreshedEvent> {

    public static final String GREETINGS = "greetings";
    public static final String KEY_COUNT = "key_count";

    private final ApplicationContext applicationContext;
    private final Environment environment;

    private KafkaStreams kafkaStreams;

    @Bean(name = "keyCountTable")
    public Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KTable<String, String> keyCountTable = streamsBuilder.table(
                GREETINGS,
                Consumed.with(Serdes.String(), Serdes.String()),
                Materialized.as(KEY_COUNT)
        );

        keyCountTable.toStream().print(Printed.<String, String>toSysOut().withLabel("key_count_table"));

        return streamsBuilder.build();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info("Starting Kafka Streams {}", event.getApplicationContext().getApplicationName());

        Properties streamsProperties = new Properties();

        streamsProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        streamsProperties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "3");

        streamsProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try {
            streamsProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "key_count_k_table_topology");
            kafkaStreams = new KafkaStreams(applicationContext.getBean("keyCountTable", Topology.class), streamsProperties);

            kafkaStreams.start();
        } catch (Exception e) {
            log.error("Error starting Kafka Streams", e);
        }
        log.info("Kafka Streams started");
    }

    @PreDestroy
    public void close() {
        log.info("Closing Kafka Streams");
        kafkaStreams.close();
        log.info("Kafka Streams closed");
    }


}
