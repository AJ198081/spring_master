package dev.aj.ecommerce.auth.controllers;

import dev.aj.ecommerce.auth.domain.dtos.AuthResponseDto;
import dev.aj.ecommerce.auth.domain.dtos.UserLoginDto;
import dev.aj.ecommerce.auth.domain.dtos.UserRegistrationDto;
import dev.aj.ecommerce.auth.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationDto> register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(userRegistrationDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        return ResponseEntity.ok(authService.login(userLoginDto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
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
}
