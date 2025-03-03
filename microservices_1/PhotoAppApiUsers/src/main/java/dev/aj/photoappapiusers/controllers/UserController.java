package dev.aj.photoappapiusers.controllers;

import dev.aj.photoappapiusers.domain.dto.UserRegistrationRequestDto;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationResponseDto;
import dev.aj.photoappapiusers.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final Environment environment;

    @GetMapping(path = "/status/check")
    public String test(HttpServletRequest request) {
        return "Test checked!! %s, with secret %s and expiry %s".formatted(
                request.getLocalPort(),
                environment.getProperty("jwt.secret"),
                environment.getProperty("jwt.expiration.ms")
        );
    }

    @GetMapping(path = "/status/tested")
    public String tested(HttpServletRequest request) {
        return "Test passed!! %s, with secret %s and expiry %s".formatted(
                request.getLocalPort(),
                environment.getProperty("jwt.secret"),
                environment.getProperty("jwt.expiration.ms")
        );
    }

    @PostMapping(path = "/register")
    public ResponseEntity<UserRegistrationResponseDto> register(@Validated @RequestBody UserRegistrationRequestDto userRegistrationRequestDto) {
        System.out.println(userRegistrationRequestDto);

        UserRegistrationResponseDto registrationResponse = userService.saveUser(userRegistrationRequestDto);

        return ResponseEntity.ok(registrationResponse);
    }

}
