package dev.aj.full_stack_v6.common.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Auditing configuration for JPA auditing (auditor and time provider).
 * What was the issue?
 * - In a previous test context, Spring failed during boot with:
 *   NoSuchBeanDefinitionException: No bean named 'auditingEntityConfig' available
 *   while creating the 'auditorProvider' bean defined in this class.
 * Why can that happen?
 * - Non-static @Bean methods are invoked on an instance of the configuration class. That
 *   requires Spring to create/register a bean instance of this @Configuration (with the
 *   implicit name 'auditingEntityConfig') and call the method on it.
 * - In some sliced/minimal test configurations the configuration class itself might not be
 *   registered as a bean (or proxying is disabled), so there is no instance available for
 *   Spring to call the @Bean method on, resulting in the above error.
 * How does making the @Bean methods static fix it?
 * - Static @Bean methods do not require an instance of the configuration class. Spring can
 *   invoke the static factory method directly during context initialization. That removes
 *   the need for a bean named 'auditingEntityConfig' and avoids the failure.
 * - This is safe here because these beans do not rely on any instance state of the
 *   configuration class.
 * Notes:
 * - Static @Bean methods work well with @Configuration(proxyBeanMethods = false) and can
 *   reduce proxy overhead. Avoid inter-bean instance method calls when using static methods.
 */
@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "dateTimeProvider")
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