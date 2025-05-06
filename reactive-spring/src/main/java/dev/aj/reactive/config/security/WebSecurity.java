package dev.aj.reactive.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebSecurity {

    @Bean
    public SecurityWebFilterChain httpSecurityWebFilterChain(ServerHttpSecurity http,
                                                             ReactiveAuthenticationManager authenticationManager) {

        return http
                .authorizeExchange(exchange -> {
                            exchange.pathMatchers("/actuator/**").permitAll()
                                    .pathMatchers(HttpMethod.POST, "/users/**").permitAll()
//                                    .pathMatchers(HttpMethod.POST, "/authentication/login").permitAll()
                                    .anyExchange().authenticated();
                        }
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .build();
    }
}
