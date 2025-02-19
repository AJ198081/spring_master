package dev.aj.full_stack_v3.controller;

import dev.aj.full_stack_v3.PostgresTCConfig;
import dev.aj.full_stack_v3.TestConfig;
import dev.aj.full_stack_v3.TestData;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "/application-test.properties", properties = {
        "logging.level.dev.aj.full_stack_v3.controller.AuthControllerTest=debug"
})
@Import(value = {PostgresTCConfig.class, TestConfig.class, TestData.class})
class AuthControllerTest {

    private static final String AUTHORIZATION_ENDPOINT = "/api/v1/auth";

    @LocalServerPort
    private int port;

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestData testData;

    private RestClient restClient;

    @BeforeAll
    void init() {
        restClient = testConfig.restClient("http://localhost:%d/%s".formatted(port, AUTHORIZATION_ENDPOINT));
    }

    @Test
    void successfullyRegistersUserWithValidRegistrationRequest() {

        UserRegistrationRequest testUserRequest = testData.getUserRegistrationRequestStream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unable to generate test data"));

        ResponseEntity<UserRegistrationResponse> registrationResponse = restClient.post()
                .uri("/register")
                .body(testUserRequest)
                .retrieve()
                .toEntity(UserRegistrationResponse.class);

        assertTrue(registrationResponse.getStatusCode().is2xxSuccessful());
        assertNotNull(registrationResponse.getHeaders().getLocation());

        assertThat(registrationResponse.getHeaders().getLocation().toString())
                .contains(testUserRequest.getUsername());

        Assertions.assertThat(registrationResponse.getBody()).isNotNull()
                .extracting(UserRegistrationResponse::getUsername, UserRegistrationResponse::getEmail)
                .containsExactly(testUserRequest.getUsername(), testUserRequest.getEmail());

        String path = registrationResponse.getHeaders().getLocation().getPath();
        String uriPath = path.substring(path.lastIndexOf('/'));

        ResponseEntity<UserRegistrationResponse> userRegistrationResponse = restClient.get()
                .uri(uriPath)
                .retrieve()
                .toEntity(UserRegistrationResponse.class);

        Assertions.assertThat(userRegistrationResponse.getBody()).isNotNull();
        Assertions.assertThat(userRegistrationResponse.getBody())
                .usingRecursiveComparison()
                .isEqualTo(registrationResponse.getBody());
    }


    @Test
    void invalidEmailRegistrationRequestThrowsBadRequest() {
        UserRegistrationRequest testUserRequest = testData.getUserRegistrationRequestStream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unable to generate test data"));

        UserRegistrationRequest invalidEmail = UserRegistrationRequest.builder()
                .firstName(testUserRequest.getFirstName())
                .lastName(testUserRequest.getLastName())
                .email("pw")
                .username(testUserRequest.getUsername())
                .password(testUserRequest.getPassword())
                .build();

        RestClient.ResponseSpec responseSpecification = restClient.post()
                .uri("/register")
                .body(invalidEmail)
                .retrieve();

        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpClientErrorException.BadRequest.class, () -> responseSpecification.toEntity(UserRegistrationResponse.class));
    }

    @Test
    void invalidPasswordRegistrationRequestThrowsBadRequest() {

        UserRegistrationRequest testUserRequest = testData.getUserRegistrationRequestStream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unable to generate test data"));

        UserRegistrationRequest invalidPassword = UserRegistrationRequest.builder()
                .firstName(testUserRequest.getFirstName())
                .lastName(testUserRequest.getLastName())
                .email(testUserRequest.getEmail())
                .username(testUserRequest.getUsername())
                .password("abg2389@!")
                .build();

        RestClient.ResponseSpec responseSpecification = restClient.post()
                .uri("/register")
                .body(invalidPassword)
                .retrieve();

        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpClientErrorException.BadRequest.class, () -> responseSpecification.toEntity(UserRegistrationResponse.class));
    }

    @Test
    void invalidUsernameRegistrationRequestThrowsBadRequest() {

        UserRegistrationRequest testUserRequest = testData.getUserRegistrationRequestStream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unable to generate test data"));

        UserRegistrationRequest invalidUsername = UserRegistrationRequest.builder()
                .firstName(testUserRequest.getFirstName())
                .lastName(testUserRequest.getLastName())
                .email(testUserRequest.getEmail())
                .username("Ab1!")
                .password(testUserRequest.getPassword())
                .build();

        RestClient.ResponseSpec responseSpecification = restClient.post()
                .uri("/register")
                .body(invalidUsername)
                .retrieve();

        org.junit.jupiter.api.Assertions.assertThrows(org.springframework.web.client.HttpClientErrorException.BadRequest.class, () -> responseSpecification.toEntity(UserRegistrationResponse.class));
    }


}