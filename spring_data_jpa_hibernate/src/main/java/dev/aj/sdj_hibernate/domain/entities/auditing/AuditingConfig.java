package dev.aj.sdj_hibernate.domain.entities.auditing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("AJ");
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(Instant.now());
    }
}
