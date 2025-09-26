package dev.aj.full_stack_v6.security.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    private final JwtUtils jwtUtils;
    private final Environment environment;

    public void addRefreshTokenCookieToResponse(jakarta.servlet.http.HttpServletResponse response, String username) {
        String refreshToken = jwtUtils.generateRefreshJwt(username);
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(environment.getProperty("jwt.refresh.token.cookie.http.only", Boolean.class, true));
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Integer.parseInt(environment.getRequiredProperty("jwt.refresh.token.cookie.max.age")));
        response.addCookie(refreshTokenCookie);
    }


    public boolean requestContainsValidRefreshTokenCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getUsernameFromRefreshTokenCookie(HttpServletRequest request) {
        if (requestContainsValidRefreshTokenCookie(request)) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    return jwtUtils.getUsernameFromJwt(cookie.getValue());
                }
            }
        }
        return null;
    }

    public void invalidateRefreshTokenCookie(HttpServletRequest request, HttpServletResponse response) {
        if (requestContainsValidRefreshTokenCookie(request)) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
