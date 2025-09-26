package dev.aj.full_stack_v6.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieUtils {

    void addRefreshTokenCookieToResponse(jakarta.servlet.http.HttpServletResponse response, String username);

    boolean requestContainsValidRefreshTokenCookie(HttpServletRequest request);

    String getUsernameFromRefreshTokenCookie(HttpServletRequest request);

    void invalidateRefreshTokenCookie(HttpServletRequest request, HttpServletResponse response);
}
