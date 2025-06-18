package dev.aj.full_stack_v5.auth.controllers;

import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.InitSecurityUser;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.auth.domain.dtos.LoginRequestDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UpdateUserDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class, InitSecurityUser.class})
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserControllerTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private Environment environment;

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RestClient restClient;

    private String validJwtAccessToken;

    @Autowired
    private InitSecurityUser initSecurityUser;

    private HttpHeaders bearerTokenHeader;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));
        bearerTokenHeader = initSecurityUser.getBearerTokenHeader(restClient);
    }

    @AfterAll
    void tearDown() {
        restClient = null;
        validJwtAccessToken = null;
    }

    @Test
    void registerUser() {
        UserRegistrationDto userRegistrationDto = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(1)
                .findFirst().orElseThrow(() -> new NoSuchElementException("Unable to generate stream of UserRegistrationDtos"));

        ResponseEntity<UserResponseDto> userRegistrationResponse = restClient.post()
                .uri("/api/v1/users/")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .body(userRegistrationDto)
                .retrieve()
                .toEntity(UserResponseDto.class);

        assertNotNull(userRegistrationResponse.getBody());
        Assertions.assertEquals(HttpStatus.CREATED, userRegistrationResponse.getStatusCode());
        Assertions.assertNotNull(userRegistrationResponse.getBody());
        Assertions.assertEquals(userRegistrationDto.getUsername(), userRegistrationResponse.getBody().getUsername());
        Assertions.assertEquals(userRegistrationDto.getRoles(), userRegistrationResponse.getBody().getRoles());

    }

    private @NotNull String getBearerTokenHeader() {
        return environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(" ")
                .concat(validJwtAccessToken);
    }

    @Test
    void getUserByUsername() {
        UserRegistrationDto userRegistrationDto = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(1)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Unable to generate Stream of UserRegistrationDtos"));

        ResponseEntity<UserResponseDto> fetchedUserResponse = restClient.post()
                .uri("/api/v1/users/")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .body(userRegistrationDto)
                .exchange((request, response) -> restClient.get()
                        .uri("/api/v1/users/username/{username}", userRegistrationDto.getUsername())
                        .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                        .retrieve()
                        .toEntity(UserResponseDto.class)
                );

        assertNotNull(fetchedUserResponse);
        Assertions.assertEquals(HttpStatus.OK, fetchedUserResponse.getStatusCode());
        assertNotNull(fetchedUserResponse.getBody());
        Assertions.assertEquals(userRegistrationDto.getUsername(), fetchedUserResponse.getBody().getUsername());
        Assertions.assertEquals(userRegistrationDto.getRoles(), fetchedUserResponse.getBody().getRoles());
    }

    @Test
    void getUserByUsernameFromExistingUsersList() {
        ResponseEntity<List<UserResponseDto>> allUsersResponse = restClient.get()
                .uri("/api/v1/users/all")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(allUsersResponse.getBody());
        Assertions.assertFalse(allUsersResponse.getBody().isEmpty());
        UserResponseDto firstUser = allUsersResponse.getBody().getFirst();
        String username = firstUser.getUsername();

        ResponseEntity<UserResponseDto> userResponse = restClient.get()
                .uri("/api/v1/users/username/{username}", username)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(UserResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        assertNotNull(userResponse.getBody());
        Assertions.assertEquals(username, userResponse.getBody().getUsername());
        Assertions.assertEquals(firstUser.getRoles(), userResponse.getBody().getRoles());
    }

    @Test
    void deleteUserByUsername() {
        UserRegistrationDto userRegistrationDto = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(1)
                .findFirst().orElseThrow(() -> new NoSuchElementException("Unable to generate Stream of UserRegistrationDtos"));

        ResponseEntity<UserResponseDto> userRegistrationResponse = restClient.post()
                .uri("/api/v1/users/")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .body(userRegistrationDto)
                .retrieve()
                .toEntity(UserResponseDto.class);

        assertNotNull(userRegistrationResponse.getBody());
        String username = userRegistrationResponse.getBody().getUsername();

        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/v1/users/username/{username}", username)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Void.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        RestClient.ResponseSpec responseSpec = restClient.get()
                .uri("/api/v1/users/username/{username}", username)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve();

        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> responseSpec.toEntity(UserResponseDto.class));
    }

    @Test
    void isUsernameTaken() {
        ResponseEntity<List<UserResponseDto>> allUsersResponse = restClient.get()
                .uri("/api/v1/users/all")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(allUsersResponse.getBody());
        Assertions.assertFalse(allUsersResponse.getBody().isEmpty());
        String existingUsername = allUsersResponse.getBody().getFirst().getUsername();

        ResponseEntity<Boolean> takenResponse = restClient.get()
                .uri("/api/v1/users/check-username/{username}", existingUsername)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Boolean.class);

        Assertions.assertEquals(HttpStatus.OK, takenResponse.getStatusCode());
        assertNotNull(takenResponse.getBody());
        Assertions.assertTrue(takenResponse.getBody());

        String randomUsername = "nonexistent_" + System.currentTimeMillis();

        ResponseEntity<Boolean> notTakenResponse = restClient.get()
                .uri("/api/v1/users/check-username/{username}", randomUsername)
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(Boolean.class);

        Assertions.assertEquals(HttpStatus.OK, notTakenResponse.getStatusCode());
        assertNotNull(notTakenResponse.getBody());
        Assertions.assertFalse(notTakenResponse.getBody());
    }

    @Test
    void updateUser() {

        ResponseEntity<List<UserResponseDto>> registeredUsers = restClient.get()
                .uri("/api/v1/users/all")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(registeredUsers.getBody());
        UserResponseDto registeredUser = registeredUsers.getBody().stream().findAny().orElseThrow();

        UpdateUserDto updatedUserDto = testDataFactory.getUpdatedUser(registeredUser);

        ResponseEntity<UserResponseDto> updatedUserResponse = restClient.patch()
                .uri("/api/v1/users/")
                .headers(httpHeaders -> httpHeaders.addAll(bearerTokenHeader))
                .body(updatedUserDto)
                .retrieve()
                .toEntity(UserResponseDto.class);

        Assertions.assertEquals(HttpStatus.OK, updatedUserResponse.getStatusCode());
        Assertions.assertNotNull(updatedUserResponse.getBody());

        Set<String> updatedRoles = updatedUserResponse.getBody().getRoles();

        Assertions.assertAll(
                () -> assertThat(updatedRoles)
                        .isEqualTo(updatedUserDto.getRolesToBeUpdated()),

                () -> assertThat(updatedUserResponse.getBody().getRoles())
                        .usingRecursiveAssertion()
                        .isEqualTo(updatedUserDto.getRolesToBeUpdated())
        );
    }
}
