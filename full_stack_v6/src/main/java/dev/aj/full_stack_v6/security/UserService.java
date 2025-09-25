package dev.aj.full_stack_v6.security;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticatedPrincipal;

public interface UserService {
    void createUser(UserCreateRequest userCreateRequest);
    boolean isUserExists(String username);
    @PreAuthorize("isAuthenticated()")
    void deleteUser(String username,
                    AuthenticatedPrincipal principal);

    @PreAuthorize("isAuthenticated()")
    void updateUser(UserCreateRequest userCreateRequest,
                    AuthenticatedPrincipal principal);

    @PreAuthorize("isAuthenticated()")
    void changePassword(String username,
                        String password,
                        AuthenticatedPrincipal principal);
}
