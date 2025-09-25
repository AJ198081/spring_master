package dev.aj.full_stack_v6;


import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@TestConfiguration
@Slf4j
@RequiredArgsConstructor
public class UserAuthFactory {

    private final TestConfig testConfig;
    private final TestDataFactory testDataFactory;
    private final Environment environment;

    private static UserCreateRequest currentUserCreateRequest;
    private static String currentJwt;

    private RestClient authClient;
    private RestClient userClient;

    public RestClient secureRestClient(String baseUrl, Integer port) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.addAll(this.getBearerTokenHeader(port)))
                .build();

    }

    public UserCreateRequest addANewUniqueUser(Integer port) {
        if (userClient == null) {
            userClient = testConfig.restClient("http://localhost:%d%s".formatted(port, "/api/v1/users"));
        }

        UserCreateRequest userCreateRequest = testDataFactory.getStreamOfUserRequests()
                .limit(1)
                .findFirst()
                .orElseThrow();

        try {
            ResponseEntity<Void> response = postANewUser(userCreateRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return addANewUniqueUser(port);
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.error("User already existed: {}; trying again.", e.getMessage());
            return addANewUniqueUser(port);
        }

        return userCreateRequest;
    }

    public HttpHeaders getBearerTokenHeader(Integer port) {

        String bearerToken = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(getExistingOrNewJWT(port));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);

        return httpHeaders;
    }

    public @Nullable String currentUsername() {
        return currentUserCreateRequest != null ? currentUserCreateRequest.username() : null;
    }

    public @NonNull String getCurrentUsername(Integer port) {
        if (currentUserCreateRequest != null) return currentUserCreateRequest.username();
        addANewUniqueUser(port);
        return currentUserCreateRequest.username();
    }



    private @NotNull ResponseEntity<Void> postANewUser(UserCreateRequest userCreateRequest) {

        return userClient.post()
                .uri("/")
                .body(userCreateRequest)
                .retrieve()
                .toBodilessEntity();
    }

    private String getExistingOrNewJWT(Integer port) {
        if (authClient == null) {
            authClient = testConfig.restClient("http://localhost:%d%s".formatted(port, "/api/v1/auths"));
        }

        if (currentJwt != null) {
            return currentJwt;
        }

        currentUserCreateRequest = addANewUniqueUser(port);
        LoginRequest loginRequest = new LoginRequest(currentUserCreateRequest.username(), currentUserCreateRequest.getPassword());

        ResponseEntity<LoginResponse> response = authClient.post()
                .uri("/login")
                .body(loginRequest)
                .retrieve()
                .toEntity(LoginResponse.class);

        currentJwt = Objects.requireNonNull(response.getBody(), "Login attempt unsuccessful").jwt();
        return currentJwt;
    }
}
