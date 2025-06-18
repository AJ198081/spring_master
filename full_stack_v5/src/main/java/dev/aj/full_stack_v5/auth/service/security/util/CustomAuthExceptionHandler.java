package dev.aj.full_stack_v5.auth.service.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
// AuthenticationEntryPoint is a bit of a misnomer, it should be AuthenticationExceptionHandler as it gets triggered when an AuthenticationException is encountered
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final String message = "Unauthenticated User, exception : %s".formatted(authException.getMessage());
        log.error(message);
        final Map<String, Object> body = new HashMap<>();
        body.put("error", authException.getClass().getSimpleName());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
