package dev.aj.full_stack_v2.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;

public class GrantedAuthoritySerializer extends StdSerializer<SimpleGrantedAuthority> {

    protected GrantedAuthoritySerializer(StdSerializer<?> src) {
        super(src);
    }

    protected GrantedAuthoritySerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    public GrantedAuthoritySerializer(Class<SimpleGrantedAuthority> t) {
        super(t);
    }

    protected GrantedAuthoritySerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serialize(SimpleGrantedAuthority value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.getAuthority());
    }
}
