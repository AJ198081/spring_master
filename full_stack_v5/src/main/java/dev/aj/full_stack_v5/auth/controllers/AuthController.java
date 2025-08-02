package dev.aj.full_stack_v5.auth.controllers;

import dev.aj.full_stack_v5.auth.domain.dtos.LoginRequestDto;
import dev.aj.full_stack_v5.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){
        return ResponseEntity.ok(authService.authenticateUser(loginRequestDto.getUsername(), loginRequestDto.getPassword(), response));
    }

    @GetMapping("/login")
    public ResponseEntity<String> accessToken(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticateUser(username, password, response));
    }

    @GetMapping("/login/refresh-token")
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.getAccessTokenFromRefreshToken(request));
    }

}
