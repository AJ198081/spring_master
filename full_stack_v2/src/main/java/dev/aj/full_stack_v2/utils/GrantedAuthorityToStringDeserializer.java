package dev.aj.full_stack_v2.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;

@Configuration
public class GrantedAuthorityToStringDeserializer {

 /*   @Bean
    Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.deserializerByType(GrantedAuthority.class, new JsonDeserializer<String>() {
            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                return p.getValueAsString().toUpperCase();
            }
        });
    }*/

    @Bean
    Jackson2ObjectMapperBuilderCustomizer customizer2() {
        return jackson2ObjectMapperBuilder -> jackson2ObjectMapperBuilder
                .serializerByType(GrantedAuthority.class, new JsonSerializer<GrantedAuthority>() {
                    @Override
                    public void serialize(GrantedAuthority value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        gen.writeString(value.getAuthority());
                    }
                });
    }

}
