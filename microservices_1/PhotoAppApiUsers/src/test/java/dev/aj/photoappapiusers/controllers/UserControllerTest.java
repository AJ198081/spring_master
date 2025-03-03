package dev.aj.photoappapiusers.controllers;

import dev.aj.photoappapiusers.PostgresTCConfig;
import dev.aj.photoappapiusers.TestConfig;
import dev.aj.photoappapiusers.TestData;
import dev.aj.photoappapiusers.domain.dto.UserLoginRequestDto;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationRequestDto;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationResponseDto;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(value = {PostgresTCConfig.class, TestConfig.class, TestData.class})
@TestPropertySource(locations = "classpath:application-test.properties", properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "logging.level.root=off",
        "logging.level.org.springframework.security=trace"
})
class UserControllerTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private TestData testData;

    @Autowired
    private TestConfig testConfig;

    @BeforeAll
    void beforeAll() {
        restClient = testConfig.restClient("http://localhost:%d/api/users/".formatted(port));
    }

    @Test
    void register() {
        UserRegistrationRequestDto userRegistrationRequestDto = testData.getUserRegistrationStream().limit(1).toList().stream().findFirst().orElseThrow();
        ResponseEntity<UserRegistrationResponseDto> responseEntity = restClient.post()
                .uri("/register")
                .body(userRegistrationRequestDto)
                .retrieve()
                .toEntity(UserRegistrationResponseDto.class);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());

        Assertions.assertThat(responseEntity).isNotNull()
                .extracting("body")
                .usingRecursiveComparison()
                .ignoringFields("password", "userId", "role")
                .isEqualTo(userRegistrationRequestDto);

        Assertions.assertThat(responseEntity.getBody()).isNotNull()
                .extracting("role")
                .isEqualTo("ROLE_".concat(userRegistrationRequestDto.getRole().toUpperCase()));

        Assertions.assertThat(responseEntity.getBody()).isNotNull()
                .extracting("userId")
                .isInstanceOfSatisfying(String.class,
                        userId -> assertThatCode(() -> UUID.fromString(userId))
                                .doesNotThrowAnyException()
                );

        ResponseEntity<String> loginResponse = restClient.post()
                .uri("/login")
                .body(UserLoginRequestDto.builder()
                        .usernameOrEmail(userRegistrationRequestDto.getUsername())
                        .password(userRegistrationRequestDto.getPassword())
                        .build())
                .retrieve()
                .toEntity(String.class);

        assertNotNull(loginResponse);
        assertEquals(200, loginResponse.getStatusCode().value());
    }

    @Test
    @Disabled("This test is disabled because it depends on the API Gateway")
    void registerViaAPIGateway() {

        RestClient apiGatewayRestClient = testConfig.restClient("http://localhost:8080/api/users");
        Awaitility.await()
                .atLeast(Duration.ofSeconds(50))
                .and()
                .untilAsserted(() -> {
                    assertEquals(200, apiGatewayRestClient.get()
                            .uri("/status/check")
                            .retrieve()
                            .toEntity(String.class)
                            .getStatusCodeValue());
                });
        UserRegistrationRequestDto userRegistrationRequestDto = testData.getUserRegistrationStream().limit(1).toList().stream().findFirst().orElseThrow();
        ResponseEntity<UserRegistrationResponseDto> responseEntity = apiGatewayRestClient
                .post()
                .uri("/register")
                .body(userRegistrationRequestDto)
                .retrieve()
                .toEntity(UserRegistrationResponseDto.class);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());

        Assertions.assertThat(responseEntity).isNotNull()
                .extracting("body")
                .usingRecursiveComparison()
                .ignoringFields("password", "userId")
                .isEqualTo(userRegistrationRequestDto);

        Assertions.assertThat(responseEntity.getBody()).isNotNull()
                .extracting("userId")
                .isInstanceOfSatisfying(String.class,
                        userId -> assertThatCode(() -> UUID.fromString(userId))
                                .doesNotThrowAnyException()
                );
    }
}