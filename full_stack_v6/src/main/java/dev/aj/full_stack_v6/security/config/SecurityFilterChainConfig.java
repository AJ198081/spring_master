package dev.aj.full_stack_v6.security.config;

import dev.aj.full_stack_v6.security.config.entry_points.AuthEntryPointJwt;
import dev.aj.full_stack_v6.security.config.filters.AuthTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityFilterChainConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthTokenFilter authTokenFilter,
            AuthEntryPointJwt authEntryPointJwt
    ) throws Exception {

        return http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/api/v1/auths/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/users/").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/users/usernameTaken/*").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(CsrfConfigurer::disable)
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                .sessionManagement(httpSessionConfigurer ->
                        httpSessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

}
