package dev.aj.full_stack_v5.auth.controllers;

import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.auth.domain.dtos.UpdateUserDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class})
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class UserControllerTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));

        testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(5)
                .forEach(userRegistration -> {
                    ResponseEntity<UserResponseDto> userRegistrationResponse = restClient.post()
                            .uri("/api/v1/users/")
                            .body(userRegistration)
                            .retrieve()
                            .toEntity(UserResponseDto.class);

                    log.info("Added users: {}", userRegistrationResponse.getBody());
                });
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }

    @Test
    void registerUser() {
        UserRegistrationDto userRegistrationDto = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(1)
                .findFirst().orElseThrow(() -> new NoSuchElementException("Unable to generate stream of UserRegistrationDtos"));

        ResponseEntity<UserResponseDto> userRegistrationResponse = restClient.post()
                .uri("/api/v1/users/")
                .body(userRegistrationDto)
                .retrieve()
                .toEntity(UserResponseDto.class);

        assertNotNull(userRegistrationResponse.getBody());
        Assertions.assertEquals(HttpStatus.CREATED, userRegistrationResponse.getStatusCode());
        Assertions.assertNotNull(userRegistrationResponse.getBody());
        Assertions.assertEquals(userRegistrationDto.getUsername(), userRegistrationResponse.getBody().getUsername());
        Assertions.assertEquals(userRegistrationDto.getRoles(), userRegistrationResponse.getBody().getRoles());

    }

    @Test
    void getUserByUsername() {

        UserRegistrationDto userRegistrationDto = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(1)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Unable to generate Stream of UserRegistrationDtos"));

        ResponseEntity<UserResponseDto> fetchedUserResponse = restClient.post()
                .uri("/api/v1/users/")
                .body(userRegistrationDto)
                .exchange((request, response) -> restClient.get()
                        .uri("/api/v1/users/username/{username}", userRegistrationDto.getUsername())
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
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(allUsersResponse.getBody());
        Assertions.assertFalse(allUsersResponse.getBody().isEmpty());
        UserResponseDto firstUser = allUsersResponse.getBody().getFirst();
        String username = firstUser.getUsername();

        ResponseEntity<UserResponseDto> userResponse = restClient.get()
                .uri("/api/v1/users/username/{username}", username)
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
                .body(userRegistrationDto)
                .retrieve()
                .toEntity(UserResponseDto.class);

        assertNotNull(userRegistrationResponse.getBody());
        String username = userRegistrationResponse.getBody().getUsername();

        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/api/v1/users/username/{username}", username)
                .retrieve()
                .toEntity(Void.class);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        RestClient.ResponseSpec responseSpec = restClient.get()
                .uri("/api/v1/users/username/{username}", username)
                .retrieve();

        Assertions.assertThrows(HttpClientErrorException.NotFound.class, () -> responseSpec.toEntity(UserResponseDto.class));
    }

    @Test
    void isUsernameTaken() {
        ResponseEntity<List<UserResponseDto>> allUsersResponse = restClient.get()
                .uri("/api/v1/users/all")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(allUsersResponse.getBody());
        Assertions.assertFalse(allUsersResponse.getBody().isEmpty());
        String existingUsername = allUsersResponse.getBody().getFirst().getUsername();

        ResponseEntity<Boolean> takenResponse = restClient.get()
                .uri("/api/v1/users/check-username/{username}", existingUsername)
                .retrieve()
                .toEntity(Boolean.class);

        Assertions.assertEquals(HttpStatus.OK, takenResponse.getStatusCode());
        assertNotNull(takenResponse.getBody());
        Assertions.assertTrue(takenResponse.getBody());

        String randomUsername = "nonexistent_" + System.currentTimeMillis();

        ResponseEntity<Boolean> notTakenResponse = restClient.get()
                .uri("/api/v1/users/check-username/{username}", randomUsername)
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
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertNotNull(registeredUsers.getBody());
        UserResponseDto registeredUser = registeredUsers.getBody().stream().findAny().orElseThrow();

        UpdateUserDto updatedUserDto = testDataFactory.getUpdatedUser(registeredUser);

        ResponseEntity<UserResponseDto> updatedUserResponse = restClient.put()
                .uri("/api/v1/users/")
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
