package dev.aj.hibernate_jpa.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    
    /**
     * Configures the SecurityFilterChain for HTTP security.
     *
     * <p>This method defines access rules for various HTTP methods and endpoints.
     * The {@code requestMatchers} method is used to specify the URL patterns,
     * and it is noteworthy that the servlet context-path is automatically excluded
     * when matching the request URLs. For example, if the application's context-path
     * is "/api", a request to "/api/employee/123" matches the pattern "/employee/**"
     * without needing to include "/api" in the pattern.</p>
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if there is an issue in configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        return http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/employee/**", "/student/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/employee/**", "/student/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/employee/**", "/student/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/employee/**", "/student/**").hasRole("ADMIN")
                        )
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
