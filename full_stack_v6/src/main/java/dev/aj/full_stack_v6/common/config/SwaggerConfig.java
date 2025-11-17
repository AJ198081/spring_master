package dev.aj.full_stack_v6.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
                        .addSecuritySchemes("bearer-jwt", securityScheme))
                .info(new Info()
                        .title("Orders APIs")
                        .version("v1.0.0")
                        .summary("Publicly exposed APIs for the Ordering Application")
                        .description("""
                                RESTful APIs for managing customers, vendors, carts, payments, and orders operations in the Ordering Application.
                                These endpoints allow customers to browse products, manage shopping carts, place orders, and process payments.
                                Includes secure authentication and authorization mechanisms using JWT tokens.
                                """))
                .tags(List.of(
                                new Tag().name("Checkout")
                                        .description("Checkout and order management related operations"),
                                new Tag().name("AuthN")
                                        .description("Authentication and authorization-related operations"),
                                new Tag().name("Cart")
                                        .description("Cart-management related operations"),
                                new Tag().name("Product")
                                        .description("Product-management operations"),
                                new Tag().name("Customer")
                                        .description("Customer-management operations"),
                                new Tag().name("Vendor")
                                        .description("Vendor-management operations"),
                                new Tag().name("Payment")
                                        .description("Payment-management operations")
                ));
    }

}
