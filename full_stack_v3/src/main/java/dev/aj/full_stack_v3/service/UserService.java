package dev.aj.full_stack_v3.service;

import dev.aj.full_stack_v3.domain.dto.UserLoginRequest;
import dev.aj.full_stack_v3.domain.dto.UserLoginResponse;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import org.springframework.security.core.Authentication;

public interface UserService {

    UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest);

    UserRegistrationResponse getUserByUsername(String username);

    UserRegistrationResponse getUserByUsernameOrEmail(String usernameOrEmail);

    UserLoginResponse loginUser(UserLoginRequest userLoginRequest);

    UserLoginResponse loginUser(Authentication authentication);
}
