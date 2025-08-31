package dev.aj.full_stack_v5.auth.service;

import dev.aj.full_stack_v5.auth.domain.dtos.UpdateUserDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.auth.domain.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    UserResponseDto registerUser(UserRegistrationDto userRegistrationDto);

    User registerNewUser(UserRegistrationDto userRegistrationDto);
    UserResponseDto getUserByUsername(String username);
    User getUserByTheUsername(String username);
    Optional<User> findUserByUsername(String username);
    UserResponseDto getUserById(Long id);
    void deleteUserById(Long id);
    void deleteUserByUsername(String username);
    boolean isUsernameTaken(String username);
    UserResponseDto updateUser(UpdateUserDto updateUserDto);
    List<UserResponseDto> getAllUsers();
    void resetPassword(String username, String email, String newPassword);

    void addRoles(User user, Set<String> roles);
}
