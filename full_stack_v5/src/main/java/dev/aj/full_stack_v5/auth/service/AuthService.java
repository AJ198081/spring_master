package dev.aj.full_stack_v5.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    String authenticateUser(String username, String password, HttpServletResponse response);

    String getAccessTokenFromRefreshToken(HttpServletRequest request);
}
