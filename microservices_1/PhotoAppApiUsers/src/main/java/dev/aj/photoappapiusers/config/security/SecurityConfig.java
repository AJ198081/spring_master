package dev.aj.photoappapiusers.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.photoappapiusers.config.security.filters.LoginAuthenticationFilter;
import dev.aj.photoappapiusers.services.impl.CustomUserDetailsService;
import dev.aj.photoappapiusers.utils.JwtUtils;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper,
            JwtUtils jwtUtils,
            AuthenticationManager authenticationManager
    ) throws Exception {

        httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);


        LoginAuthenticationFilter loginAuthenticationFilter = new LoginAuthenticationFilter(objectMapper, authenticationManager, jwtUtils);
        loginAuthenticationFilter.setFilterProcessesUrl("/api/users/login");

        return httpSecurity
                .authorizeHttpRequests(authorizeRequests -> {
                            authorizeRequests
//                                    .requestMatchers(HttpMethod.POST, "/api/users/register").access(new WebExpressionAuthorizationManager("hasIpAddress('localhost:8080')"))
                                    .requestMatchers("/actuator/**").permitAll()
                                    .requestMatchers("/api/users/register").permitAll()
                                    .requestMatchers("/api/users/login").permitAll()
                                    .requestMatchers("/api/users/status/check").permitAll()
                                    .requestMatchers("/api/users/status/tested").permitAll()
                                    .requestMatchers("/api/**").authenticated();
                        }
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilter(loginAuthenticationFilter)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(authenticationEntryPoint()))
                .build();
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return new BasicAuthenticationEntryPoint();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
