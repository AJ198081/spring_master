package dev.aj.full_stack_v6.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Add JWT for token-based authentication");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearer-jwt");

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", securityScheme));
    }
}
