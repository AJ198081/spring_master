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

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    private final TestConfig testConfig;
    private final TestDataFactory testDataFactory;
    private final Environment environment;

    private static UserCreateRequest currentUserCreateRequest;
    private static String currentJwt;

    private RestClient authClient;
    private RestClient userClient;

    public void setClients(Integer port) {
        if (authClient == null) {
            authClient = testConfig.restClient("http://localhost:%d%s".formatted(port, environment.getProperty("AUTH_API_PATH")));
        }

        if (userClient == null) {
            userClient = testConfig.restClient("http://localhost:%d%s".formatted(port, environment.getProperty("USER_API_PATH")));
        }
    }

    public void resetClients() {
        authClient = null;
        userClient = null;
    }

    public RestClient authenticatedRestClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.addAll(this.getBearerTokenHeader()))
                .build();
    }

    public RestClient newAuthenticateAdminRestClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.addAll(this.getAdminBearerTokenHeader()))
                .build();
    }

    public RestClient newAuthenticatedNonAdminRestClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.addAll(this.getNonAdminBearerTokenHeader()))
                .build();
    }

    public @NotNull UserCreateRequest getAUniqueUserCreateRequest() {

        UserCreateRequest newUser;

        newUser = testDataFactory.getStreamOfUserRequests()
                .limit(1)
                .findFirst()
                .orElseThrow();

        if (userExists(newUser)) {
            return getAUniqueUserCreateRequest();
        }

        return newUser;
    }

    private Boolean userExists(UserCreateRequest userCreateRequest) {
        return userClient.get()
                .uri("/usernameTaken/{username}", userCreateRequest.username())
                .retrieve()
                .toEntity(Boolean.class)
                .getBody();
    }

    public UserCreateRequest addANewUniqueUserWithAnyRole() {
        deleteCurrentUser();
        currentUserCreateRequest = testDataFactory.getStreamOfUserRequests()
                .limit(1)
                .findFirst()
                .orElseThrow();

        try {
            ResponseEntity<Void> response = postANewUser(currentUserCreateRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return addANewUniqueUserWithAnyRole();
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.error("User already existed: {}; trying again.", e.getMessage());
            return addANewUniqueUserWithAnyRole();
        }

        return currentUserCreateRequest;
    }

    public UserCreateRequest addAUniqueAdminUser() {
        deleteCurrentUser();
        currentUserCreateRequest = testDataFactory.getStreamOfUserRequests()
                .filter(user -> user.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(ROLE_ADMIN)))
                .limit(1)
                .findFirst()
                .orElseThrow();

        try {
            ResponseEntity<Void> response = postANewUser(currentUserCreateRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                return addAUniqueAdminUser();
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.error("Admin user already existed: {}; trying again.", e.getMessage());
            return addAUniqueAdminUser();
        }

        return currentUserCreateRequest;
    }

    public HttpHeaders getBearerTokenHeader() {

        String bearerToken = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(getExistingOrNewJWT());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);

        return httpHeaders;
    }

    public HttpHeaders getAdminBearerTokenHeader() {
        String bearerToken = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(loginAndReturnAdminJwt());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);
        return httpHeaders;
    }

    public HttpHeaders getNonAdminBearerTokenHeader() {
        String bearerToken = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer ")
                .concat(loginAndReturnNonAdminJwt());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, bearerToken);
        return httpHeaders;
    }

    public @Nullable String currentUsername() {
        return currentUserCreateRequest != null ? currentUserCreateRequest.username() : null;
    }

    public void deleteCurrentUser() {
        currentUserCreateRequest = null;
        currentJwt = null;
    }

    public @NonNull String getCurrentUsername() {
        if (currentUserCreateRequest != null) return currentUserCreateRequest.username();
        addANewUniqueUserWithAnyRole();
        return currentUserCreateRequest.username();
    }


    private @NotNull ResponseEntity<Void> postANewUser(UserCreateRequest userCreateRequest) {

        return userClient.post()
                .uri("/")
                .body(userCreateRequest)
                .retrieve()
                .toBodilessEntity();
    }

    private String getExistingOrNewJWT() {

        if (currentJwt == null) {

            currentUserCreateRequest = addANewUniqueUserWithAnyRole();
            LoginRequest loginRequest = new LoginRequest(currentUserCreateRequest.username(), currentUserCreateRequest.getPassword());

            loginAndRefreshCurrentJwt(loginRequest);
        }


        return currentJwt;
    }

    public void loginAsDifferentNewUser(String role, String notThisUsername) {
        if (Objects.isNull(currentUserCreateRequest)
                || currentUserCreateRequest.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals(role))
                || currentUserCreateRequest.username().equals(notThisUsername)) {
            addANewUniqueUser(role);
        }

        LoginRequest loginRequest = new LoginRequest(
                currentUserCreateRequest.username(),
                currentUserCreateRequest.getPassword()
        );

        loginAndRefreshCurrentJwt(loginRequest);
    }


    private void addANewUniqueUser(String role) {
        deleteCurrentUser();

        currentUserCreateRequest = testDataFactory.getStreamOfUserRequests()
                .filter(userRequest -> userRequest.getAuthorities()
                        .stream()
                        .anyMatch(authority -> authority.getAuthority().equals(role)))
                .limit(1)
                .findFirst()
                .orElseThrow();

        try {
            ResponseEntity<Void> response = postANewUser(currentUserCreateRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
                addANewUniqueUser(role);
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.error("User already existed with the same non-admin role: {}; trying again.", e.getMessage());
            addANewUniqueUser(role);
        }
    }

    public String loginAndReturnAdminJwt() {
        deleteCurrentUser();
        currentUserCreateRequest = testDataFactory.getStreamOfUserRequests()
                .filter(userRequest -> userRequest.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN)))
                .limit(1)
                .findFirst()
                .orElseThrow();

        try {
            ResponseEntity<Void> response = postANewUser(currentUserCreateRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
               return loginAndReturnAdminJwt();
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Admin user already existed: {}", currentUserCreateRequest.username());
            return loginAndReturnAdminJwt();
        }

        LoginRequest loginRequest = new LoginRequest(currentUserCreateRequest.username(), currentUserCreateRequest.getPassword());
        loginAndRefreshCurrentJwt(loginRequest);
        return currentJwt;
    }

    public RestClient getAuthClient() {
        Objects.requireNonNull(authClient, "Auth client is null");
        return authClient;
    }

    private void loginAndRefreshCurrentJwt(LoginRequest loginRequest) {
        ResponseEntity<LoginResponse> response = authClient.post()
                .uri("/login")
                .body(loginRequest)
                .retrieve()
                .toEntity(LoginResponse.class);

        currentJwt = Objects.requireNonNull(response.getBody(), "Login attempt unsuccessful").jwt();
    }

    public String loginAndReturnNonAdminJwt() {
        deleteCurrentUser();
        currentUserCreateRequest = testDataFactory.getStreamOfUserRequests()
                .filter(userRequest -> userRequest.getAuthorities()
                        .stream()
                        .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(ROLE_ADMIN)))
                .limit(1)
                .findFirst()
                .orElseThrow();

         try {
            ResponseEntity<Void> response = postANewUser(currentUserCreateRequest);
            if (!response.getStatusCode().is2xxSuccessful()) {
               return loginAndReturnNonAdminJwt();
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Non-Admin user already existed: {}", currentUserCreateRequest.username());
            return loginAndReturnNonAdminJwt();
        }

        LoginRequest loginRequest = new LoginRequest(currentUserCreateRequest.username(), currentUserCreateRequest.getPassword());
        loginAndRefreshCurrentJwt(loginRequest);
        return currentJwt;
    }
}
