package dev.aj.full_stack_v5.auth.controllers;

import dev.aj.full_stack_v5.auth.domain.dtos.UpdateUserDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.auth.domain.mapper.UserMapper;
import dev.aj.full_stack_v5.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final UserMapper userMapper;

    @PostMapping("/")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto userRegistrationDto){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(userRegistrationDto));
    }

    @PutMapping("/")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.updateUser(updateUserDto));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @DeleteMapping("/username/{username}")
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> isUsernameTaken(@PathVariable String username) {
        return ResponseEntity.ok(userService.isUsernameTaken(username));
    }
}
