package dev.aj.ecommerce.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.ecommerce.PostgresTCConfig;
import dev.aj.ecommerce.TestConfig;
import dev.aj.ecommerce.TestData;
import dev.aj.ecommerce.auth.domain.dtos.AuthResponseDto;
import dev.aj.ecommerce.auth.domain.dtos.UserLoginDto;
import dev.aj.ecommerce.auth.domain.dtos.UserRegistrationDto;
import dev.aj.ecommerce.auth.domain.entities.User;
import dev.aj.ecommerce.auth.domain.entities.enums.Role;
import dev.aj.ecommerce.auth.repositories.AuthRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@Import(value = {PostgresTCConfig.class, TestConfig.class, TestData.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestConfig testConfig;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeAll
    void beforeAll() {
        restClient = testConfig.restClient("http://localhost:" + port);
    }

    @BeforeEach
    void setUp() {
        // Clear any existing test users
        authRepository.findByUsername(TEST_USERNAME).ifPresent(authRepository::delete);
        authRepository.findByEmail(TEST_EMAIL).ifPresent(authRepository::delete);
    }

    @Test
    void testRegisterUser() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername(TEST_USERNAME);
        registrationDto.setEmail(TEST_EMAIL);
        registrationDto.setPassword(TEST_PASSWORD);
        registrationDto.setRole("USER");

        ResponseEntity<UserRegistrationDto> registrationResult = restClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(registrationDto)
                .retrieve()
                .toEntity(UserRegistrationDto.class);

        Assertions.assertThat(registrationResult).isNotNull()
                .extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.CREATED);

        Assertions.assertThat(registrationResult).isNotNull()
                .extracting(ResponseEntity::getBody).usingRecursiveAssertion()
                .ignoringFields("id", "password", "role");

        // Verify user was saved to the database
        User savedUser = authRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, savedUser.getPassword())).isTrue(); // Password should be encoded
    }

    @Test
    void testLoginUser() throws Exception {
        // Create a user first
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setEmail(TEST_EMAIL);
        user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        user.setRole(Role.ROLE_USER);
        authRepository.save(user);

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);

        ResponseEntity<AuthResponseDto> loginResponse = restClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginDto)
                .retrieve()
                .toEntity(AuthResponseDto.class);

        // Extract tokens for next test
        AuthResponseDto response = loginResponse.getBody();

        assertThat(response).isNotNull();


        // Test token refresh
        ResponseEntity<AuthResponseDto> refreshTokenResponse = restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/auth/refresh-token")
                        .queryParam("refreshToken", response.getRefreshToken())
                        .build())
                .retrieve()
                .toEntity(AuthResponseDto.class);

        // Test logout
        ResponseEntity<Void> logoutResponse = restClient.post().uri("/api/auth/logout")
                .header("Authorization", "Bearer " + response.getAccessToken())
                .retrieve()
                .toBodilessEntity();

       Assertions.assertThat(logoutResponse).isNotNull()
               .extracting(ResponseEntity::getStatusCode)
               .isEqualTo(HttpStatus.OK);
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsernameOrEmail("nonexistent");
        loginDto.setPassword("wrongpassword");

        RestClient.ResponseSpec loginRequestSpec = restClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginDto)
                .retrieve();

        Assertions.assertThatThrownBy(loginRequestSpec::toBodilessEntity);
    }
}