package dev.aj.sdj_hibernate.domain.repositories;

import dev.aj.sdj_hibernate.InitConfiguration;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.User;
import dev.aj.sdj_hibernate.domain.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(value = {PostgresConfiguration.class, InitConfiguration.class})
@SpringBootTest
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;


    @BeforeAll
    void setUp() {
        userRepository.save(User.builder()
                .username("test_user_first")
                .email("test_user@example.com")
                .build());
        
        userRepository.save(User.builder()
                .username("test_user_1")
                .email("test_user_1@example.com")
                .build());
    
        userRepository.save(User.builder()
                .username("test_user_2")
                .email("test_user_2@example.com")
                .build());
    
        userRepository.save(User.builder()
                .username("test_user_3")
                .email("test_user_3@example.com")
                .build());
    
        userRepository.save(User.builder()
                .username("test_user_4")
                .email("test_user_4@example.com")
                        .level(2)
                .build());
    
        userRepository.save(User.builder()
                .username("test_user_5")
                .email("test_user_5@example.com")
                        .level(3)
                .build());
    }

    @AfterAll
    void tearDownClass() {
        userRepository.deleteAll();
    }

    @Test
    void testPersistUser() {
        // Arrange
        User user = User.builder()
                .username("test_user_new")
                .email("test_user_2@example.com")
                .level(1)
                .build();

        // Act
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
        assertEquals(user.getEmail(), foundUser.getEmail());
        Assertions.assertThat(userRepository.count()).isGreaterThan(1L);

        List<User> usersWithLevelOneOrTwo = userRepository.findUsersByLevelIn(List.of(1, 2));
        Assertions.assertThat(usersWithLevelOneOrTwo.size()).isGreaterThan(0);

        List<User> usersWithLevelZero = userRepository.findUsersByLevelIn(List.of(0));
        Assertions.assertThat(usersWithLevelZero.size()).isGreaterThan(2);

        List<User> usersWithLevelOneOrTwoOrderByLevelDesc = userRepository.findUsersByLevelInOrderByLevelDesc(List.of(0, 1, 2, 3));

        Assertions.assertThat(usersWithLevelOneOrTwoOrderByLevelDesc.size()).isGreaterThan(5);

        List<User> inactiveOrFirstLevelUsers = userRepository.findUsersByLevelInOrIsActiveFalse(List.of(1));

        Assertions.assertThat(inactiveOrFirstLevelUsers.size()).isGreaterThan(0);
    }


    @Test
    void testPagination() {
        Page<User> userPageResult = userRepository.findAll(PageRequest.of(1, 10, Sort.by("level").descending().and(Sort.by("username").ascending())));
        Assertions.assertThat(userPageResult.getNumberOfElements()).isGreaterThan(1);
    }

    @Test
    void testVersionDoesNotUpdateWithModifyingQueries() {
        User firstUser = userRepository.findAll().getFirst();
        Long initialUserVersion = firstUser.getVersion();

        userService.updateUsernameById(firstUser.getId(), firstUser.getUsername().concat(" updated"));

        Optional<User> updatedUser = userRepository.findById(firstUser.getId());

        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getVersion()).isEqualTo(initialUserVersion);
        Assertions.assertThat(updatedUser.get().getUsername()).isEqualTo(firstUser.getUsername().concat(" updated"));
    }

    @Test
    void testVersionDoesUpdateWithModifyingQueries() {
        User firstUser = userRepository.findAll().getFirst();
        Long initialUserVersion = firstUser.getVersion();

        userService.updateUsernameAndVersionById(firstUser.getId(), firstUser.getUsername().concat(" updated"));

        Optional<User> updatedUser = userRepository.findById(firstUser.getId());

        Assertions.assertThat(updatedUser.isPresent()).isTrue();
        Assertions.assertThat(updatedUser.get().getVersion()).isGreaterThan(initialUserVersion);
        Assertions.assertThat(updatedUser.get().getUsername()).isEqualTo(firstUser.getUsername().concat(" updated"));
    }



}