package dev.aj.full_stack_v5;

import dev.aj.full_stack_v5.auth.domain.dtos.LoginRequestDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;

import java.util.NoSuchElementException;
import java.util.Set;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Slf4j
@Import({TestDataFactory.class})
public class InitSecurityUser {

    private final TestDataFactory testDataFactory;
    private final UserService userService;
    private final Environment environment;

    public LoginRequestDto registerUserAndReturnLoginDto() {

        UserRegistrationDto userRegistrationDto = testDataFactory.generateStreamOfUserRegistrationDtos()
                .limit(1)
                .peek(user -> user.setRoles(Set.of("ADMIN")))
                .findFirst().orElseThrow(() -> new NoSuchElementException("Unable to generate a stream of UserRegistrationDtos"));

        // Get the raw password so it can be used to call the login endpoint
        LoginRequestDto validLoginRequestDto = LoginRequestDto.builder()
                .username(userRegistrationDto.getUsername())
                .password(userRegistrationDto.getPassword())
                .build();

        UserResponseDto userResponseDto = userService.registerUser(userRegistrationDto);

        String rolesOfUser = userResponseDto.getRoles()
                .stream()
                .reduce((first, second) -> first + ", " + second)
                .orElseThrow(() -> new NoSuchElementException("Unable to reduce roles to a single string. No roles found."));

        log.info("Registered user: '{}' as an '{}'", userResponseDto.getUsername(), rolesOfUser);

        return validLoginRequestDto;
    }

    public String getValidJwtToken(RestClient restClient, LoginRequestDto loginRequestDto) {

        ResponseEntity<String> jwtAccessToken = restClient.post()
                .uri("/api/v1/auth/login")
                .body(loginRequestDto)
                .retrieve()
                .toEntity(String.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(HttpStatus.OK, jwtAccessToken.getStatusCode()),
                () -> Assertions.assertNotNull(jwtAccessToken.getBody()),
                () -> Assertions.assertFalse(jwtAccessToken.getBody().isEmpty())
        );
        log.info("Valid JWT Access Token: '{}'", jwtAccessToken.getBody());
        return jwtAccessToken.getBody();
    }

    public HttpHeaders getBearerTokenHeader(@NonNull String jwtAccessToken) {
        String bearerToken = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(" ")
                .concat(jwtAccessToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);

        return httpHeaders;
    }

    public HttpHeaders getBearerTokenHeader(RestClient restClient) {

        String bearerToken = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(" ")
                .concat(getValidJwtToken(restClient, registerUserAndReturnLoginDto()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);

        return httpHeaders;
    }
}
