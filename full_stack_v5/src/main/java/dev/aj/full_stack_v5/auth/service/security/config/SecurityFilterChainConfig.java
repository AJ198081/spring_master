package dev.aj.full_stack_v5.auth.service.security.config;

import dev.aj.full_stack_v5.auth.service.security.exceptions.CustomAuthExceptionHandler;
import dev.aj.full_stack_v5.auth.service.security.filter.AuthTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!local")
public class SecurityFilterChainConfig {

    private final CustomAuthExceptionHandler authExceptionHandler;
    private final AuthenticationProvider authenticationProvider;
    private final AuthTokenFilter authTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        AuthenticationManagerBuilder authManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.authenticationProvider(authenticationProvider);

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exceptionHandler -> exceptionHandler.authenticationEntryPoint(authExceptionHandler))
                .sessionManagement(sessionConfigurer -> sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/users/**",
                                "/api/v1/images/download/**"
                        )
                            .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**")
                            .permitAll()
                        .requestMatchers(HttpMethod.GET, "/*", "/assets/*")
                            .permitAll()
                        .anyRequest()
                            .authenticated()
                )
                .build();
    }

}
