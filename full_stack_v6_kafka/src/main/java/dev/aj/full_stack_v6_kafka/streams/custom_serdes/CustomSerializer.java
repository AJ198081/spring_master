package dev.aj.full_stack_v6_kafka.streams.custom_serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v6_kafka.streams.custom_types.StringObject;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
public record CustomSerializer(ObjectMapper objectMapper) implements Serializer<StringObject> {

    @SneakyThrows
    @Override
    public byte[] serialize(String topic, StringObject data) {
        return objectMapper.writeValueAsBytes(data);
    }
}
