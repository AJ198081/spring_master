package dev.aj.security_management;

import com.github.javafaker.Faker;
import dev.aj.security_management.security.entities.SecurityUser;
import dev.aj.security_management.security.service.SecurityUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.stream.Stream;

@TestComponent
@Import(value = {TestDataConfig.class})
@RequiredArgsConstructor
public class InitDatabase {

    private final Faker faker;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUserService securityUserService;

    @PostConstruct
    public void init() {

      List<SecurityUser> securityUsers = List.of(
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
        );

        List<SecurityUser> savedSecurityUsers = securityUserService.saveAll(securityUsers);

        savedSecurityUsers.stream()
                .map(SecurityUser::getUsername)
                .forEach(System.out::println);

    }
}
