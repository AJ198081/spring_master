package dev.aj.reactive.controllers;

import dev.aj.reactive.domain.dtos.LoginRequest;
import dev.aj.reactive.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Mono<Map<String, String>>> login(@RequestBody Mono<LoginRequest> loginRequest) {

        Mono<Map<String, String>> authenticationResponse = loginRequest
                .flatMap(request -> authenticationService.authenticate(request.getEmail(), request.getPassword()));

        return ResponseEntity.ok(authenticationResponse);
    }

    @RequestMapping("/logout")
    public ResponseEntity<Mono<Void>> logout() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
