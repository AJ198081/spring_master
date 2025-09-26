package dev.aj.full_stack_v6.security.utils;

import dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions.JwtValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtUtils {
    @Nullable
    String getJwt(HttpServletRequest request);

    String generateJwt(UserDetails userDetails);

    String getUsernameFromJwt(String jwt);

    boolean validateJwt(String jwt) throws JwtValidationException;

    String generateRefreshJwt(String username);
}
