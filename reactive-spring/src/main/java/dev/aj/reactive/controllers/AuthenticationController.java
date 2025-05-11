package dev.aj.reactive.controllers;

import dev.aj.reactive.domain.dtos.LoginRequest;
import dev.aj.reactive.services.JwtService;
import dev.aj.reactive.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Mono<Map<String, Object>>> login(@RequestBody Mono<LoginRequest> loginRequest) {

        return ResponseEntity.ok(
                loginRequest
                        .flatMap(request -> reactiveAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())))
                        .map(authentication ->
                                Map.of("principal", authentication.getPrincipal(),
                                        "token", jwtService.generateToken(authentication.getName()),
                                        "user", userService.findByEmail(authentication.getName()))
                        )
        );
    }

    @RequestMapping("/logout")
    public ResponseEntity<Mono<Void>> logout() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/all")
    public ResponseEntity<Mono<String>> getAllUsers() {
        return ResponseEntity.ok(Mono.just("All users"));
    }
}
