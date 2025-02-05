package dev.aj.full_stack_v2.config.jackson;

import dev.aj.full_stack_v2.utils.GrantedAuthorityDeserializer;
import dev.aj.full_stack_v2.utils.GrantedAuthoritySerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Configuration
public class CustomDeserializers {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            builder.deserializerByType(GrantedAuthority.class, new GrantedAuthorityDeserializer(SimpleGrantedAuthority.class));
            builder.serializerByType(GrantedAuthority.class, new GrantedAuthoritySerializer(SimpleGrantedAuthority.class));
        };
    }
}
