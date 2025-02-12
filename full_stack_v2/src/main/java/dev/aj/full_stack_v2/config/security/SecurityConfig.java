package dev.aj.full_stack_v2.config.security;

import dev.aj.full_stack_v2.config.security.filters.CustomLoggingFilter;
import dev.aj.full_stack_v2.config.security.filters.JwtAuthTokenFilter;
import dev.aj.full_stack_v2.config.security.filters.UserAgentFilter;
import dev.aj.full_stack_v2.config.security.jwt.JWTAuthEntryPoint;
import dev.aj.full_stack_v2.services.impl.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   PasswordEncoder passwordEncoder,
                                                   SecurityUserService securityUserService,
                                                   CustomLoggingFilter loggingFilter,
                                                   UserAgentFilter userAgentFilter,
                                                   JWTAuthEntryPoint jwtAuthEntryPoint,
                                                   JwtAuthTokenFilter jwtAuthTokenFilter,
                                                   CorsConfigurationSource corsConfigurationSource) throws Exception {

        AuthenticationManagerBuilder authManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder
                .userDetailsService(securityUserService)
                .passwordEncoder(passwordEncoder);

        AuthenticationManager authManager = authManagerBuilder.build();

        return httpSecurity
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(HttpMethod.PUT, "/api/admin/role")
                            .hasRole("ADMIN")
                        .requestMatchers("/api/csrf-token","/api/auth/public/login", "/api/auth/public/signup")
                        .permitAll()
                        .anyRequest()
                            .authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
                .addFilterBefore(jwtAuthTokenFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(userAgentFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(loggingFilter, BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .ignoringRequestMatchers("/api/admin/**", "/api/notes/**", "/api/auth/public/**")
                )
                .exceptionHandling(exceptionHandler -> exceptionHandler
                                .authenticationEntryPoint(jwtAuthEntryPoint))
                .authenticationManager(authManager)
                .sessionManagement(sessionCustomizer -> sessionCustomizer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity httpSecurity,
                                                           PasswordEncoder passwordEncoder,
                                                           SecurityUserService securityUserService) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder
                .userDetailsService(securityUserService)
                .passwordEncoder(passwordEncoder);

        return authManagerBuilder.build();
    }

    @Bean
    public UserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new UrlBasedCorsConfigurationSource();
    }



}
