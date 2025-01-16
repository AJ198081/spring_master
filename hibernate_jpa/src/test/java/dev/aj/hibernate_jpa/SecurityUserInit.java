package dev.aj.hibernate_jpa;

import com.github.javafaker.Faker;
import dev.aj.hibernate_jpa.entities.security.RoleLevel;
import dev.aj.hibernate_jpa.entities.security.SecurityUser;
import dev.aj.hibernate_jpa.repositories.impl.SecurityUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class SecurityUserInit {

    private final Faker faker;
    private final SecurityUserRepository securityUserRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    void initSecurityUser() {

        if (securityUserRepository.count() > 0) {
            return;
        }

        List<SecurityUser> tenSecurityUsers = getSecurityUsers().limit(10)
                .toList();

        securityUserRepository.saveAll(tenSecurityUsers);

        securityUserRepository.saveAll(List.of(
                SecurityUser.builder()
                        .username("TL")
                        .password(passwordEncoder.encode("password"))
                        .authorities(List.of("ROLE_USER"))
                        .build(),
                SecurityUser.builder()
                        .username("PW")
                        .password(passwordEncoder.encode("password"))
                        .authorities(List.of("ROLE_ADMIN"))
                        .build()
        ));
    }

    private Stream<SecurityUser> getSecurityUsers() {

        return Stream.generate(() -> SecurityUser.builder()
                .username(faker.name().username())
                .password(passwordEncoder.encode(faker.internet().password()))
                .authorities(getAuthorities())
                .build()
        );
    }

    private List<String> getAuthorities() {
        List<RoleLevel> availableRoleLevels = Arrays.stream(RoleLevel.values()).toList();
        return availableRoleLevels.stream()
                .limit(faker.random().nextInt(1, 3))
                .map(RoleLevel::toString)
                .toList();
    }
}