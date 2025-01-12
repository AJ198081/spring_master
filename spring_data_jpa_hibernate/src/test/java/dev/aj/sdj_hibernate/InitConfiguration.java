package dev.aj.sdj_hibernate;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.aspects.LogExecutionTiming;
import dev.aj.sdj_hibernate.domain.entities.User;
import dev.aj.sdj_hibernate.domain.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Slf4j
public class InitConfiguration {

    private static final Faker faker = new Faker(Locale.of("en", "IND"));
    private final UserRepository userRepository;

    @PostConstruct
    @LogExecutionTiming // Doesn't work, likely because spring aop
    void init() {
        populateUsersInDatabase();
        updateUsersInDatabase();
    }

    private void populateUsersInDatabase() {
        userRepository.saveAll(
                generateUsers()
                        .limit(50)
                        .filter(user -> !user.getIsActive())
                        .toList()
        );
        userRepository.flush();
        long count = userRepository.count();
        log.info("User count: {}", count);
    }

    private void updateUsersInDatabase() {
        userRepository.findAll().stream()
                .limit(10)
                .filter(user -> !user.getIsActive())
                .peek(user -> {
                    user.setIsActive(true);
                }).forEach(userRepository::save);
    }

    private Stream<User> generateUsers() {

        return Stream.generate(() -> User.builder()
                .username(faker.name().username())
                .email(faker.internet().emailAddress())
                .level(faker.random().nextInt(0, 5))
                .isActive(faker.random().nextBoolean())
                .registrationDate(LocalDate.from(faker.date().birthday(18, 780).toInstant().atZone(ZoneId.systemDefault())))
                .build());
    }

    @Bean
    public AuditorAware<String> userRepository() {
        List<String> availableUserNames = List.of("TL", "PW", "JS", "DR", "DV", "MP");
        String currentUserName = availableUserNames.get(RandomUtils.nextInt(0, availableUserNames.size()));
        return () -> Optional.of(currentUserName);
    }

}
