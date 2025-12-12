package dev.aj.full_stack_v3.config.security;

import dev.aj.full_stack_v3.config.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   UserDetailsService userDetailsService,
                                                   PasswordEncoder passwordEncoder,
                                                   JwtFilter jwtFilter,
                                                   CorsConfigurationSource corsConfigurationSource,
                                                   AuthenticationEntryPoint authenticationEntryPoint
    ) throws Exception {

        httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return httpSecurity
                .csrf(CsrfConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer
                        .configurationSource(corsConfigurationSource)
                )
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/api/v1/auth/login",
                                        "/api/v1/auth/logout",
                                        "/api/v1/auth/register",
                                        "/oauth2/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
//                .oauth2Login(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Named("encodePassword")
    public String encodePassword(String password) {
        return passwordEncoder().encode(password);
    }

}
