package dev.aj.full_stack_v6.security.services.impl;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.security.UserService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

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
            throw new EntityExistsException("User already exists: %s");
        }

        userDetailsManager.createUser(userCreateRequest.encodePassword(passwordEncoder));

    }

    @Override
    public boolean isUserExists(String username) {
        return userDetailsManager.userExists(username);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public void updateUser(@NonNull UserCreateRequest userCreateRequest,
                           AuthenticatedPrincipal principal) {
        log.info("Received request to update user: {}", userCreateRequest.username());

        UserDetails userDetails = userDetailsManager.loadUserByUsername(userCreateRequest.username());

        if (isNeitherAdminNorCurrentUser(principal, userDetails)) {
            log.error("User: {} is not allowed to update user: {}", principal.getName(), userCreateRequest.username());
            throw new SecurityException("User is not allowed to update user: %s".formatted(userCreateRequest.username()));
        }

        userDetailsManager.updateUser(userCreateRequest);
    }

    @Override
    public void deleteUser(String username, AuthenticatedPrincipal principal) {
        log.info("Received request to delete user: {}", username);
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        if (isNeitherAdminNorCurrentUser(principal, userDetails)) {
            log.error("User: {} is not allowed to delete user: {}", principal.getName(), username);
            throw new SecurityException("User is not allowed to delete user: %s".formatted(username));
        }

        userDetailsManager.deleteUser(username);
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public void changePassword(String username,
                               String password,
                               AuthenticatedPrincipal principal) {

        log.info("Received request to change password for user: {}", username);

        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        if (isNeitherAdminNorCurrentUser(principal, userDetails)) {
            log.error("User: {} is not allowed to change the password for user: {}", principal.getName(), username);
            throw new SecurityException("User is not allowed to change password for user: %s".formatted(username));
        }

        userDetailsManager.changePassword(userDetails.getUsername(), password);
    }

    private static boolean isNeitherAdminNorCurrentUser(AuthenticatedPrincipal principal, UserDetails userDetails) {
        return !userDetails.getUsername().equals(principal.getName())
                && userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .noneMatch(UserServiceImpl::isAdmin);
    }


    private static boolean isAdmin(String role) {
        return role.equals("ROLE_ADMIN");
    }
}
