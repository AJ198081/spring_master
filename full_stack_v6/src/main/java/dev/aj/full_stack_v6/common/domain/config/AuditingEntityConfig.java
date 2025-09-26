package dev.aj.full_stack_v6.common.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "dateTimeProvider")
public class AuditingEntityConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof SecurityUser user) {
                return Optional.of("%s-%s".formatted(user.getUsername(), user.getRoles()));
            }*/
            return Optional.of("AJ");
        };
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(java.time.ZonedDateTime.now());
    }

}