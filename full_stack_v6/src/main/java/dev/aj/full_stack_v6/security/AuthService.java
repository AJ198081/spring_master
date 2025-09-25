package dev.aj.full_stack_v6.security;

import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import dev.aj.full_stack_v6.common.domain.events.UserLogoutEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.modulith.events.ApplicationModuleListener;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest, jakarta.servlet.http.HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);

    void processLogoutEvent(UserLogoutEvent userLogoutEvent);
}
