package dev.aj.full_stack_v6.common.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Disabling proxying for the beans defined in this class.
 * Static beans that are called every time, without Spring proxying them,
 * especially for the auditorProvider, which needs to look at the SecurityContextHolder for each thread.
 */
//TODO: Spring Modulith throwing unable to instantiate bean exception. Clean it up once it's fixed.
@Configuration(proxyBeanMethods = false)
public class AuditingEntityConfig {

    /**
     * Provides the current auditor for entity auditing.
     *
     * @return AuditorAware instance that supplies the current user identifier
     */
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

    /**
     * Provides the current date-time for entity auditing.
     *
     * @return DateTimeProvider instance that supplies the current ZonedDateTime
     */
    @Bean
    public static DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(java.time.ZonedDateTime.now());
    }

}