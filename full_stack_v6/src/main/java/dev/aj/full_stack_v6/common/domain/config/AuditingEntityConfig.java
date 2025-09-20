package dev.aj.full_stack_v6.common.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditingEntityConfig {

    @Bean
    public static AuditorAware<String> auditorProvider() {
        return () -> {
            /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof SecurityUser user) {
                return Optional.of("%s %s".formatted(user.getUsername(), user.getRoles()));
            }*/
            return Optional.of("AJ");
        };
    }

    @Bean
    public static DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(java.time.ZonedDateTime.now());
    }

}