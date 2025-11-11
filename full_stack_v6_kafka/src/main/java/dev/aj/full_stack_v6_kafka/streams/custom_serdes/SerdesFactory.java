package dev.aj.full_stack_v6_kafka.streams.custom_serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v6_kafka.streams.custom_types.StringObject;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SerdesFactory {

    private final ObjectMapper objectMapper;

    public Serde<String> stringSerdes() {
        return new StringSerdes();
    }

    public Serde<StringObject> jsonSerdes() {
        Serializer<StringObject> customSerializer = new CustomSerializer(objectMapper);
        Deserializer<StringObject> customDeserializer = new CustomDeserializer(objectMapper);
        return Serdes.serdeFrom(customSerializer, customDeserializer);
    }
}
