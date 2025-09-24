package dev.aj.full_stack_v6;


import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@TestConfiguration(proxyBeanMethods = false)
@Slf4j
@RequiredArgsConstructor
public class UserAuthFactory {

    private final TestConfig testConfig;
    private final TestDataFactory testDataFactory;
    private final Environment environment;

    private RestClient authClient;
    private RestClient userClient;


    public UserCreateRequest addANewUniqueUser(Integer port) {
        if (userClient == null) {
            userClient = testConfig.restClient("http://localhost:%d%s".formatted(port, "/api/v1/users"));
        }

        UserCreateRequest userCreateRequest = testDataFactory.getStreamOfUserRequests()
                .limit(1)
                .findFirst()
                .orElseThrow();

        ResponseEntity<Void> response = postANewUser(userCreateRequest);
        if (!response.getStatusCode().is2xxSuccessful()) {
            addANewUniqueUser(port);
        }
        return userCreateRequest;
    }

    public HttpHeaders getBearerTokenHeader(Integer port) {

        String bearerToken = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(" ")
                .concat(getJWT(port));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);

        return httpHeaders;
    }

    private @NotNull ResponseEntity<Void> postANewUser(UserCreateRequest userCreateRequest) {

        return userClient.post()
                .uri("/")
                .body(userCreateRequest)
                .retrieve()
                .toBodilessEntity();
    }

    private String getJWT(Integer port) {
        if (authClient == null) {
            authClient = testConfig.restClient("http://localhost:%d%s".formatted(port, "/api/v1/auths"));
        }

        UserCreateRequest userCreateRequest = addANewUniqueUser(port);
        LoginRequest loginRequest = new LoginRequest(userCreateRequest.username(), userCreateRequest.getPassword());

        ResponseEntity<LoginResponse> response = authClient.post()
                .uri("/login")
                .body(loginRequest)
                .retrieve()
                .toEntity(LoginResponse.class);

        return Objects.requireNonNull(response.getBody(), "Login attempt unsuccessful").jwt();
    }
}
