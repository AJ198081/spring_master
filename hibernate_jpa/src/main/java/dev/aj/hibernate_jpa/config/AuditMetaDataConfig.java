package dev.aj.hibernate_jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
public class AuditMetaDataConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        List<String> availableUserNames = List.of("TL", "PW", "JS", "DR", "DV", "MP");
        return () -> Optional.of(availableUserNames.get(new Random().nextInt(availableUserNames.size())));
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }

}
