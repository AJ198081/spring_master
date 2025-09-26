package dev.aj.full_stack_v6.security.services.impl;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions.UnauthorisedOperationException;
import dev.aj.full_stack_v6.security.UserService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserCreateRequest userCreateRequest) {
        log.info("Received request to create user: {}", userCreateRequest);

        if (userDetailsManager.userExists(userCreateRequest.username())) {
            throw new EntityExistsException("User already exists: %s".formatted(userCreateRequest.username()));
        }

        userDetailsManager.createUser(userCreateRequest.encodePassword(passwordEncoder));

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void updateUser(@NonNull UserCreateRequest userCreateRequest,
                           Principal principal) {
        log.info("Received request to update user: {}", userCreateRequest.username());

        if (isNeitherAdminNorCurrentUser(
                userDetailsManager.loadUserByUsername(userCreateRequest.username()),
                userCreateRequest.username())
        ) {
            log.error("User: {} is not allowed to update user: {}", principal.getName(), userCreateRequest.username());
            throw new SecurityException("User is not allowed to update user: %s".formatted(userCreateRequest.username()));
        }

        userDetailsManager.updateUser(userCreateRequest);
    }

    @SneakyThrows
    @Override
    public void deleteUser(String usernameToBeDeleted, Principal principal) {
        log.info("Received request to delete user: {}", usernameToBeDeleted);

        UserDetails loggedInUserDetails = userDetailsManager.loadUserByUsername(principal.getName());

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

        userDetailsManager.deleteUser(usernameToBeDeleted);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public void changePassword(String username,
                               String password,
                               Principal principal) {

        log.info("Received request to change password for user: {}", username);

        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        if (isNeitherAdminNorCurrentUser(userDetails, username)) {
            log.error("User: {} is not allowed to change the password for user: {}", principal.getName(), username);
            throw new SecurityException("User is not allowed to change password for user: %s".formatted(username));
        }

        userDetailsManager.changePassword(userDetails.getPassword(), password);
    }

    @Override
    public Boolean exists(String username) {
        boolean isUserExists = userDetailsManager.userExists(username);
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
}
