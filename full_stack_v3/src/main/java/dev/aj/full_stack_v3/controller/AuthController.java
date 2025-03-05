package dev.aj.full_stack_v3.controller;

import dev.aj.full_stack_v3.domain.dto.UserLoginRequest;
import dev.aj.full_stack_v3.domain.dto.UserLoginResponse;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import dev.aj.full_stack_v3.domain.mapper.UserMapper;
import dev.aj.full_stack_v3.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final Environment environment;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@RequestBody @Validated UserRegistrationRequest userRegistrationRequest) {
        log.debug("User registration request received: {}", userRegistrationRequest);


        URI createdUserUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .pathSegment("api/v1/auth", "{username}")
                .buildAndExpand(userRegistrationRequest.getUsername())
                .toUri();

        return ResponseEntity
                .created(createdUserUri)
                .body(
                        userService.registerUser(userRegistrationRequest)
                );
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserRegistrationResponse> getUserByName(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> loginRequest(@RequestBody @Validated UserLoginRequest userLoginRequest, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(userMapper.userLoginRequestToUsernamePasswordAuthenticationToken(userLoginRequest));
        String refreshToken = userService.generateRefreshTokenFromAuthentication(authentication);

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        int refreshTokenExpirationSeconds = environment.getProperty("jwt.refresh.expiration.ms", Integer.class, 300000) / 1000;
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(refreshTokenExpirationSeconds);

        ResponseEntity<UserLoginResponse> responseEntity = ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, "refreshToken=%s; HttpOnly; Max-Age=%d; Path=%s".formatted(refreshToken, refreshTokenExpirationSeconds, "/refresh-token"))
                .header(HttpHeaders.SET_COOKIE, "exploitableToken=%s; Max-Age=%d; path=%s".formatted(refreshToken, refreshTokenExpirationSeconds, "/"))
                .body(userService.loginUser(authentication));
        return responseEntity;
    }

    @GetMapping("/logout")
    public ResponseEntity<HttpStatus> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, "refreshToken=; HttpOnly; Max-Age=0; Path=%s".formatted("/refresh-token"))
                .build();
    }

}
