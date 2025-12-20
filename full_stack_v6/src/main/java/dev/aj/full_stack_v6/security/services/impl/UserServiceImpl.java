package dev.aj.full_stack_v6.security.services.impl;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.common.domain.entities.Role;
import dev.aj.full_stack_v6.common.domain.entities.User;
import dev.aj.full_stack_v6.common.domain.enums.UserRole;
import dev.aj.full_stack_v6.common.domain.mappers.UserMapper;
import dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions.UnauthorisedOperationException;
import dev.aj.full_stack_v6.security.UserService;
import dev.aj.full_stack_v6.security.repositories.RoleRepository;
import dev.aj.full_stack_v6.security.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public void createUser(UserCreateRequest userCreateRequest) {
        log.info("Received request to create user: {}", userCreateRequest.username());

        if (this.userExists(userCreateRequest.username())) {
            throw new EntityExistsException("User already exists: %s".formatted(userCreateRequest.username()));
        }

        User user = userMapper.userCreateRequestToUser(userCreateRequest);
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        assignRolesToUser(userCreateRequest.role(), user);

        userRepository.save(user);
    }

    @Override
    public Long createNewUser(UserCreateRequest userCreateRequest) {
        log.info("Received request fromGraphQL endpoint to create user: {}", userCreateRequest.username());

        if (this.userExists(userCreateRequest.username())) {
            throw new EntityExistsException("User already exists: %s".formatted(userCreateRequest.username()));
        }

        User user = userMapper.userCreateRequestToUser(userCreateRequest);
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        assignRolesToUser(userCreateRequest.role(), user);

        return userRepository.save(user)
                .getId();
    }

    private void assignRolesToUser(String roleToBeAssigned, User user) {

        roleRepository.findByRole(UserRole.valueOf(roleToBeAssigned.trim().toUpperCase()))
                .ifPresentOrElse(user::addRole,
                        () -> {
                            Role newRole = Role.builder()
                                    .role(UserRole.valueOf(roleToBeAssigned.trim().toUpperCase()))
                                    .build();
                            user.addRole(newRole);
                        });
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void updateUser(@NonNull UserCreateRequest userCreateRequest,
                           Principal principal) {
        log.info("Received request to update user: {}", userCreateRequest.username());

        if (isNeitherAdminNorCurrentUser(
                this.loadUserByUsername(userCreateRequest.username()),
                userCreateRequest.username())
        ) {
            log.error("User: {} is not allowed to update user: {}", principal.getName(), userCreateRequest.username());
            throw new SecurityException("User is not allowed to update user: %s".formatted(userCreateRequest.username()));
        }

        User existingUser = userRepository.findByUsername(userCreateRequest.username())
                .orElseThrow();

        User patchedUser = userMapper.patchUserFromUserCreateRequest(existingUser, userCreateRequest, passwordEncoder);

        assignRolesToUser(userCreateRequest.role(), patchedUser);

        userRepository.save(patchedUser);
    }

    @SneakyThrows
    @Override
    public void deleteUser(String usernameToBeDeleted, Principal principal) {
        log.info("Received request to delete user: {}", usernameToBeDeleted);

        UserDetails loggedInUserDetails = this.loadUserByUsername(principal.getName());

        if (isNeitherAdminNorCurrentUser(
                loggedInUserDetails,
                usernameToBeDeleted)
        ) {
            log.error("User: {} is not allowed to delete user: {}", loggedInUserDetails.getUsername(), usernameToBeDeleted);
            throw new UnauthorisedOperationException("User is not allowed to delete user: %s".formatted(usernameToBeDeleted));
        }

        log.info("User: {}, with Authority: {} is deleting user: {}",
                loggedInUserDetails.getUsername(),
                loggedInUserDetails.getAuthorities().stream().toList(),
                usernameToBeDeleted
        );

        userRepository.findByUsername(usernameToBeDeleted)
                .ifPresent(userRepository::delete);

        log.info("Username: {} deleted successfully.", usernameToBeDeleted);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public void changePassword(String username,
                               String password,
                               Principal principal) {

        log.info("Received request to change password for user: {}", username);

        UserDetails userDetails = this.loadUserByUsername(username);

        if (isNeitherAdminNorCurrentUser(userDetails, username)) {
            log.error("User: {} is not allowed to change the password for user: {}", principal.getName(), username);
            throw new UnauthorisedOperationException("User is not allowed to change password for user: %s".formatted(username));
        }

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                            user.setPassword(passwordEncoder.encode(password));
                            userRepository.save(user);
                            log.info("Password for user: {} has been changed", username);
                        }
                );
    }

    @Override
    public Boolean exists(String username) {
        boolean isUserExists = userRepository.existsByUsername(username);
        log.info("User: {} exists: {}", username, isUserExists);
        return isUserExists;
    }

    private static boolean isNeitherAdminNorCurrentUser(UserDetails loggedInUserDetails, String username) {
        return !username.equals(loggedInUserDetails.getUsername())
                && loggedInUserDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .noneMatch(UserServiceImpl::isAdmin);
    }


    private static boolean isAdmin(String role) {
        return role.equals("ROLE_ADMIN");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public User loadUser(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
    }

    private boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
