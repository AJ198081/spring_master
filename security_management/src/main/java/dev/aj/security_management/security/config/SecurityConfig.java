package dev.aj.security_management.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
     *<p>
     * <a href="https://docs.spring.io/spring-security/site/docs/5.5.4/guides/form-javaconfig.html">Spring Security Setup</a>
     *</p>
     *
     *  <p>This method defines access rules for various HTTP methods and endpoints.
     * The {@code requestMatchers} method is used to specify the URL patterns,
     * and it is noteworthy that the servlet context-path is automatically excluded
     * when matching the request URLs. For example, if the application's context-path
     * is "/api", a request to "/api/employee/123" matches the pattern "/employee/**"
     * without needing to include "/api" in the pattern.</p>
     *
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if there is an issue in configuration
     *

     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/security/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/security/**").hasAnyRole("USER", "ADMIN", "MANAGER")
                        .requestMatchers(HttpMethod.GET, "/management/leader").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/management/admin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                        .anyRequest().denyAll()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage("/security/login")
                        .loginProcessingUrl("/security/authenticate") //Spring Security will auto create this controller mapping
                        .permitAll()
                        .successForwardUrl("/security/home")
                )
                .logout(logoutConfigurer ->
                        logoutConfigurer
                                .logoutUrl("/security/logout")
//                                .logoutSuccessUrl("/security/login")
                                .permitAll()
                )
                .exceptionHandling(configurer -> configurer
                        .accessDeniedPage("/security/access-denied")
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
