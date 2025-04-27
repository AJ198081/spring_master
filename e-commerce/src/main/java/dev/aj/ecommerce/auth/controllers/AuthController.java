package dev.aj.ecommerce.auth.controllers;

import dev.aj.ecommerce.auth.domain.dtos.AuthResponseDto;
import dev.aj.ecommerce.auth.domain.dtos.UserLoginDto;
import dev.aj.ecommerce.auth.domain.dtos.UserRegistrationDto;
import dev.aj.ecommerce.auth.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RMapCache<String, String> refreshTokenCache;
    private final Environment environment;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationDto> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userRegistrationDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        AuthResponseDto loginResponse = authService.login(userLoginDto);
        if (!loginResponse.getRefreshToken().isBlank()) {
            refreshTokenCache.put(
                    loginResponse.getUsername(),
                    loginResponse.getRefreshToken(),
                    Long.parseLong(Objects.requireNonNull(environment.getProperty("jwt.refresh.expiration.ms"))),
                    TimeUnit.MILLISECONDS
            );

        }

        Cookie refreshTokenCookie = generateRefreshTokenCookie(loginResponse);

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshTokenCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {

        // First, remove the refresh token from the cache, because we need to get the cache key from SecurityContextHolder
        refreshTokenCache.remove(request.getUserPrincipal().getName());

        // Then, clear the security context
        authService.logout(request);

        Cookie refreshTokenCookie = generateRefreshTokenCookie(AuthResponseDto.builder().build());
        refreshTokenCookie.setHttpOnly(true);

        return ResponseEntity.ok()
                .header("Set-Cookie", refreshTokenCookie.toString())
                .build();
    }

    @RequestMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @RequestMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    @RequestMapping("/verify-email")
    public String verifyEmail() {
        return "verify-email";
    }

    private Cookie generateRefreshTokenCookie(AuthResponseDto loginResponse) {
        Cookie refreshTokenCookie = new Cookie("refreshTokenCookie", loginResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setDomain(environment.getProperty("jwt.refreshTokenCookie.domain", "localhost"));
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(Boolean.parseBoolean(environment.getProperty("jwt.refreshTokenCookie.secure", "false")));
        refreshTokenCookie.setMaxAge((int) Duration.ofMillis(Long.parseLong(Objects.requireNonNull(environment.getProperty("jwt.refresh.expiration.ms")))).toSeconds());
        return refreshTokenCookie;
    }
}
