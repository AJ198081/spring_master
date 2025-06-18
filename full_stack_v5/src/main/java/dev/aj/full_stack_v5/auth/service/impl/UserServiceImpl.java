package dev.aj.full_stack_v5.auth.service.impl;

import dev.aj.full_stack_v5.auth.domain.dtos.UpdateUserDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.dtos.UserResponseDto;
import dev.aj.full_stack_v5.auth.domain.entities.Role;
import dev.aj.full_stack_v5.auth.domain.entities.User;
import dev.aj.full_stack_v5.auth.domain.mapper.UserMapper;
import dev.aj.full_stack_v5.auth.repositories.RoleRepository;
import dev.aj.full_stack_v5.auth.repositories.UserRepository;
import dev.aj.full_stack_v5.auth.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@NullMarked
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto registerUser(UserRegistrationDto userRegistrationDto) {
        log.info("Registering user: {}", userRegistrationDto.getUsername());

        User user = Optional.of(userRegistrationDto)
                .filter(newUser -> !isUsernameTaken(newUser.getUsername()))
                .map(userMapper::userRegistrationToUser)
                .map(this::attachRolesToUser)
                .orElseThrow(() -> new IllegalArgumentException("Username %s already taken".formatted(userRegistrationDto.getUsername())));

        return userMapper.userToUserResponseDto(userRepository.save(user));
    }

    private User attachRolesToUser(@NotNull User user) {
        user.setRoles(user.getRoles()
                .stream()
                .map(role -> roleRepository.findRoleByName(role.getName())
                        .orElseGet(() ->
                                roleRepository.save(
                                        Role.builder()
                                                .name(role.getName())
                                                .build()
                                )
                        ))
                .collect(Collectors.toSet()));
        return user;
    }

    @Override
    public UserResponseDto getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .map(userMapper::userToUserResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("User with username: %s not found.".formatted(username)));
    }

    @Override
    public @NonNull User getUserByTheUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username: %s not found.".formatted(username)));
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::userToUserResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("User with id: %s not found".formatted(id)));
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.findById(id)
                .ifPresentOrElse(
                        userRepository::delete,
                        () -> log.info("User with id: {} not found. Unable to delete user.", id)
                );
    }

    @Override
    public void deleteUserByUsername(String username) {
        userRepository.findUserByUsername(username).ifPresentOrElse(
                userRepository::delete,
                () -> log.info("User with username: {} not found. Unable to delete user.", username)
        );
    }

    @Override
    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserResponseDto updateUser(UpdateUserDto updateUserDto) {

        Optional<User> existingUser = userRepository.findByUsername(updateUserDto.getUsername());

        if (existingUser.isEmpty()) {
            throw new EntityNotFoundException("User with username: %s not found.".formatted(updateUserDto.getUsername()));
        }

        User userToBeUpdated = existingUser.get();

        if (updateUserDto.getPassword() != null) {
            userToBeUpdated.setPassword(updateUserDto.getPassword());
        }

        // Expect the caller to pass a complete set of 'new' roles, for this instance it is equivalent to 'PUT' operation
        userToBeUpdated.getRoles().clear();

        userToBeUpdated.setRoles(updateUserDto.getRolesToBeUpdated()
                .stream()
                .map(mapRoleNameToRole())
                .collect(Collectors.toSet()));
        return userMapper.userToUserResponseDto(userRepository.save(userToBeUpdated));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::userToUserResponseDto).toList();
    }

    private Function<String, Role> mapRoleNameToRole() {
        return roleName -> roleRepository.findRoleByName(roleName)
                .orElseGet(() -> Role.builder()
                        .name(roleName)
                        .build());
    }

}
