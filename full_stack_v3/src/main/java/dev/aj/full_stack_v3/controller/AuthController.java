package dev.aj.full_stack_v3.controller;

import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import dev.aj.full_stack_v3.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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


}
