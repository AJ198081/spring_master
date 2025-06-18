package dev.aj.full_stack_v5.auth.controllers;

import dev.aj.full_stack_v5.PhotosFactory;
import dev.aj.full_stack_v5.InitSecurityUser;
import dev.aj.full_stack_v5.TestConfig;
import dev.aj.full_stack_v5.TestDataFactory;
import dev.aj.full_stack_v5.TestSecurityConfig;
import dev.aj.full_stack_v5.auth.domain.dtos.LoginRequestDto;
import dev.aj.full_stack_v5.auth.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
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
@TestPropertySource(locations = {"classpath:application-test.properties"}, properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired
    private TestConfig testConfig;

    @Autowired
    private TestDataFactory testDataFactory;

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InitSecurityUser setSecurityUser;

    private LoginRequestDto validUserLoginRequestDto;

    private String validRefreshToken;

    private String validRefreshTokenCookie;

    @BeforeAll
    void setUp() {
        restClient = testConfig.restClient("http://localhost:%d".formatted(port));
        validUserLoginRequestDto = setSecurityUser.initSecurityUser();
    }

    @AfterAll
    void tearDown() {
        restClient = null;
    }

    //    @RepeatedTest(value = 10, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @Test
    @Order(1)
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

        validRefreshToken = String.valueOf(jwtAccessToken.getHeaders().get("Set-Cookie")).split("=")[1];
        validRefreshTokenCookie = String.valueOf(jwtAccessToken.getHeaders().get("Set-Cookie"));
    }

    @Test
    @Order(2)
    void refreshToken() {

        Cookie cookieLocal = new Cookie("refresh_token", validRefreshTokenCookie);

        ResponseEntity<String> jwtAccessToken = restClient.get()
                .uri("/api/v1/auth/login/refresh-token")
                .cookies(cookie -> cookie.add("refresh_token", validRefreshToken))
                .retrieve()
                .toEntity(String.class);

    }

    @Test
    void refreshAccessToken() {
        ResponseEntity<String> jwtAccessToken = restClient.get()
                .uri("/api/v1/auth/login/refresh-token")
                .cookies(cookie -> cookie.add("refresh_token", validRefreshToken))
                .retrieve()
                .toEntity(String.class);
    }
}