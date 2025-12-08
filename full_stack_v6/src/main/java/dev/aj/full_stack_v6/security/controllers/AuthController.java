package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import dev.aj.full_stack_v6.security.services.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${AUTH_API_PATH}")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Login",
            description = "Login with user details and get the Authorization JWT",
            operationId = "login",
            method = "post",
            tags = {"AuthN"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials - Username and Password",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = LoginRequest.class,
                                    requiredMode = Schema.RequiredMode.REQUIRED
                            )
                    )
            )
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginDto, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(loginDto, response));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout",
            description = "Logout the user from the application",
            operationId = "logout",
            method = "post",
            tags = {"AuthN"},
            responses = {
                    @ApiResponse(responseCode = "202", description = "User logged out successfully")
            }
    )
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.accepted().build();
    }
}
