package dev.aj.full_stack_v6_kafka.streams.custom_serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v6_kafka.streams.custom_types.StringObject;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;

public record CustomDeserializer(ObjectMapper objectMapper) implements Deserializer<StringObject> {

    @SneakyThrows
    @Override
    public StringObject deserialize(String topic, byte[] data) {
        return objectMapper.readValue(data, StringObject.class);
    }

}
