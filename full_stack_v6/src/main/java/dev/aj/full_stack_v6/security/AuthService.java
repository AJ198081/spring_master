package dev.aj.full_stack_v6.security;

import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest, jakarta.servlet.http.HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
