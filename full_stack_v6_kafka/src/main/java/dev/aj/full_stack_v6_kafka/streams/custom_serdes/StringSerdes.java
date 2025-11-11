package dev.aj.full_stack_v6_kafka.streams.custom_serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

@Component
public class StringSerdes implements Serde<String> {

    @Override
    public Serializer<String> serializer() {
        return new StringSerializer();
    }

    @Override
    public Deserializer<String> deserializer() {
        return new StringDeserializer();
    }
}
