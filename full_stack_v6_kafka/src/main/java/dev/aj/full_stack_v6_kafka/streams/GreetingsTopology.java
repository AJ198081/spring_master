package dev.aj.full_stack_v6_kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GreetingsTopology {

    public static String GREETINGS = "greetings";
    public static String GREETINGS_UPPERCASE = "greetings_uppercase";

    @Bean
    public Topology upperCaseTopology() {

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamsBuilder
                .stream(GREETINGS, Consumed.with(Serdes.String(), Serdes.String()))
                .filter(this::consonants)
                .mapValues((_, value) -> value.toUpperCase())
                .peek(((key, value) -> System.out.println("key: " + key + ", VALUE: " + value)))
                .to(GREETINGS_UPPERCASE, Produced.with(Serdes.String(), Serdes.String()));

        return streamsBuilder.build();
    }

    private boolean consonants(String key, String value) {
        Set<String> vowelSet = Arrays.stream("aeiou".split(""))
                .collect(Collectors.toSet());

        return !vowelSet.contains(key.toLowerCase());
    }


}
