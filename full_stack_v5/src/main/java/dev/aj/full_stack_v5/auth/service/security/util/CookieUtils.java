package dev.aj.full_stack_v5.auth.service.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieUtils {

    public static final String REFRESH_TOKEN = "refresh_token";
    private final Environment environment;

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        log.info("Adding refresh token cookie");

        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse cannot be null");
        }

//        boolean useSecureCookie = environment.getProperty("jwt.refresh.token.cookie.secure", Boolean.class, true);
        int refreshTokenCookieMaxAge = environment.getProperty("jwt.refresh.token.cookie.max.age", Integer.class, 1_000);

        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, refreshToken);
        refreshTokenCookie.setHttpOnly(true);
//        refreshTokenCookie.setSecure(useSecureCookie);
        refreshTokenCookie.setMaxAge(refreshTokenCookieMaxAge);
        refreshTokenCookie.setDomain(environment.getProperty("jwt.refresh.token.cookie.domain", String.class, "localhost"));
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setAttribute("SameSite", "Strict");
//        String sameSite = useSecureCookie ? "Strict" : "Lax";
        response.addCookie(refreshTokenCookie);
        log.info("Refresh token cookie added");
    }

    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        log.info("Deleting refresh token cookie");
        if (response == null) {
            throw new IllegalArgumentException("HttpServletResponse cannot be null");
        }
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
        log.info("Refresh token cookie deleted");
    }

    public String getRefreshTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(REFRESH_TOKEN)) {
                    return cookie.getValue();
                }
            }
        }
        log.info("No refresh token cookie found");
        return null;
    }

    public void logCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("Cookie: {} = {}", cookie.getName(), cookie.getValue());
            }
        } else {
            log.info("No cookies found");
        }
    }
}
