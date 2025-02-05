package dev.aj.full_stack_v2.utils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;

public class GrantedAuthorityDeserializer extends StdDeserializer<SimpleGrantedAuthority> {


    public GrantedAuthorityDeserializer(Class<?> vc) {
        super(vc);
    }

    public GrantedAuthorityDeserializer(JavaType valueType) {
        super(valueType);
    }

    public GrantedAuthorityDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public SimpleGrantedAuthority deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JacksonException {
        return jsonParser.readValueAs(SimpleGrantedAuthority.class);
    }
}
