package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import dev.aj.full_stack_v6.security.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginDto, jakarta.servlet.http.HttpServletResponse response) {

        return ResponseEntity.ok(userService.login(loginDto, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response) {
        userService.logout(request, response);
        return ResponseEntity.accepted().build();
    }
}
