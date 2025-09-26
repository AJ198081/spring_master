package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    @GetMapping("/usernameTaken/{username}")
    public ResponseEntity<Boolean> usernameTaken(@PathVariable String username) {
        return ResponseEntity.ok(userService.exists(username));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username, Principal principal) {
        userService.deleteUser(username, principal);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/")
    public ResponseEntity<Void> updateUser(@RequestBody UserCreateRequest userCreateRequest, Principal principal) {
        userService.updateUser(userCreateRequest, principal);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String username,
                                               @RequestParam String password,
                                               Principal principal) {
        userService.changePassword(username, password, principal);
        return ResponseEntity.accepted().build();
    }
}
