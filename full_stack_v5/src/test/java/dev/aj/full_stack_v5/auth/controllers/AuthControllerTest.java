package dev.aj.full_stack_v5.auth.controllers;

import dev.aj.full_stack_v5.InitSecurityUser;
import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.auth.domain.dtos.LoginRequestDto;
import dev.aj.full_stack_v5.auth.service.UserService;
import dev.aj.full_stack_v5.auth.service.security.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(value = {TestDataFactory.class, PhotosFactory.class, TestConfig.class, TestSecurityConfig.class, InitSecurityUser.class})
@TestPropertySource(locations = {"classpath:application-test.properties"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private InitSecurityUser setSecurityUser;

    @Autowired
    private JwtUtils jwtUtils;

    @LocalServerPort
    private int port;

    private RestClient restClient;
    private LoginRequestDto validUserLoginRequestDto;
    private String validRefreshTokenCookie;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));
        validUserLoginRequestDto = setSecurityUser.registerUserAndReturnLoginDto();
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }

    //    @RepeatedTest(value = 10, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @Test
    void authenticateUser() {
        ResponseEntity<String> jwtAccessToken = restClient.post()
                .uri("/api/v1/auth/login")
                .body(validUserLoginRequestDto)
                .retrieve()
                .toEntity(String.class);

        Assertions.assertEquals(HttpStatus.OK, jwtAccessToken.getStatusCode());
        Assertions.assertNotNull(jwtAccessToken.getBody());
        Assertions.assertFalse(jwtAccessToken.getBody().isEmpty());

        Assertions.assertNotNull(jwtAccessToken.getHeaders().get("Set-Cookie"));

        String validRefreshToken = String.valueOf(jwtAccessToken.getHeaders().get("Set-Cookie")).split("=")[1].split(";")[0];

        Assertions.assertNotNull(validRefreshToken);
        Assertions.assertFalse(validRefreshToken.isEmpty());
        Assertions.assertTrue(jwtUtils.isJwtValid(validRefreshToken));
        validRefreshTokenCookie = String.valueOf(jwtAccessToken.getHeaders().get("Set-Cookie"));
    }

    @Test
    void accessToken() {

        authenticateUser();

        ResponseEntity<String> jwtAccessToken = restClient.get()
                .uri("/api/v1/auth/login/refresh-token")
                .cookie("refresh_token", validRefreshTokenCookie)
                .retrieve()
                .toEntity(String.class);

        Assertions.assertEquals(HttpStatus.OK, jwtAccessToken.getStatusCode());
        Assertions.assertNotNull(jwtAccessToken.getBody());
        Assertions.assertFalse(jwtAccessToken.getBody().isEmpty());
        Assertions.assertTrue(jwtUtils.isJwtValid(jwtAccessToken.getBody()));
    }
}