package dev.aj.full_stack_v5.order.domain.mappers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.ZonedDateTime;

public class CustomTimeStampSerializer extends StdSerializer<ZonedDateTime> {

    public CustomTimeStampSerializer() {
        this(null);
    }

    public CustomTimeStampSerializer(Class<ZonedDateTime> t) {
        super(t);
    }


    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(String.valueOf(value.toInstant().toEpochMilli()));

    }
}
