package dev.aj.sdj_hibernate.domain.services.impl;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.User;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import dev.aj.sdj_hibernate.domain.repositories.UserRepository;
import dev.aj.sdj_hibernate.domain.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Stream;

@DataJpaTest
@Import(value = {PostgresConfiguration.class, UserServiceImpl.class, AuditingConfig.class})
@TestPropertySource(locations = {"/application-test.properties", "/junit-platform.properties"}, properties = {
        "logging.level.root=off",
//        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.show_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Faker faker;


    @BeforeAll
    void beforeAll() {
        List<User> dummyUserList = generateDummyUsers(10).toList();
        userRepository.saveAll(dummyUserList);
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
    }

    @Test
    void deleteUserByLevel() {
        Integer deletedUserCount = userService.deleteUserByLevel(0);

        Assertions.assertThat(deletedUserCount).isEqualTo(10);
        Assertions.assertThat(userRepository.count()).isEqualTo(0L);
    }


    private Stream<User> generateDummyUsers(int count) {

        return Stream.generate(() -> User.builder().username(faker.name().username()).email(faker.internet().emailAddress()).isActive(faker.random().nextBoolean()).build()).limit(count);

    }

}