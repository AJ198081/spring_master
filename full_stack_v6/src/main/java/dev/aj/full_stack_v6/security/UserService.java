package dev.aj.full_stack_v6.security;

import dev.aj.full_stack_v6.common.domain.dtos.UserCreateRequest;
import dev.aj.full_stack_v6.common.domain.entities.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;

public interface UserService extends UserDetailsService {
    void createUser(UserCreateRequest userCreateRequest);
    Long createNewUser(UserCreateRequest userCreateRequest);

    @PreAuthorize("isAuthenticated()")
    void deleteUser(String username,
                    Principal principal);

    @PreAuthorize("isAuthenticated()")
    void updateUser(UserCreateRequest userCreateRequest,
                    Principal principal);

    @PreAuthorize("isAuthenticated()")
    void changePassword(String username,
                        String password,
                        Principal principal);

    Boolean exists(String username);

    User loadUser(String username);
}
