package dev.aj.full_stack_v3.service.impl;

import dev.aj.full_stack_v3.PostgresTCConfig;
import dev.aj.full_stack_v3.TestConfig;
import dev.aj.full_stack_v3.TestData;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:/application-test.properties", properties = {
        "logging.level.dev.aj.full_stack_v3.service.impl.UserServiceImpl=debug"
})
@Import(value = {PostgresTCConfig.class, TestConfig.class, TestData.class})
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestData testData;

    private List<UserRegistrationRequest> sampleUserRequests;

    @BeforeAll
    void beforeAll() {

        sampleUserRequests = testData.getUserRegistrationRequestStream()
                .limit(5)
                .toList();

        sampleUserRequests
                .forEach(userService::registerUser);
    }

    @Test
    void loadUserByUsername() {

        UserRegistrationRequest firstUserRequest = sampleUserRequests.getFirst();
        UserDetails userDetails = userService.loadUserByUsername(firstUserRequest.getUsername());

        Assertions.assertThat(userDetails.getUsername()).isEqualTo(firstUserRequest.getUsername());
        assertTrue(passwordEncoder.matches(firstUserRequest.getPassword(), userDetails.getPassword()));
    }

    @Test
    void invalidUsernameThrowsException() {
        Assertions.assertThatThrownBy(() -> userService.loadUserByUsername("invalidUsername"))
                .isInstanceOf(org.springframework.security.core.userdetails.UsernameNotFoundException.class)
                .hasMessage("User invalidUsername not found"); // Asserting message is dangerous, don't do it, simple.
    }
}