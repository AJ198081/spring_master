package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    public ResponseEntity<HttpStatus> saveUser(@RequestBody UserCreateRequest userCreateRequest) {
        userService.createUser(userCreateRequest);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username, AuthenticatedPrincipal principal) {
        userService.deleteUser(username, principal);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/")
    public ResponseEntity<Void> updateUser(@RequestBody UserCreateRequest userCreateRequest, AuthenticatedPrincipal principal) {
        userService.updateUser(userCreateRequest, principal);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String username,
                                               @RequestParam String password,
                                               AuthenticatedPrincipal principal) {
        userService.changePassword(username, password, principal);
        return ResponseEntity.accepted().build();
    }
}
