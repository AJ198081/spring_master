package dev.aj.full_stack_v2.config.auditing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZonedDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "dateTimeProvider")
public class JpaAuditingConfig {

    @Bean
    AuditorAware<String> auditorProvider() {
        return () -> {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                return Optional.of("AJ");
            } else if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
                return Optional.of("REGISTRATION");
            }else {
                return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
            }
        };
    }

    @Bean
    DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }
}
