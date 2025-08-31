package dev.aj.full_stack_v5.auth.service.security.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
// AuthenticationEntryPoint is a bit of a misnomer, it should be AuthenticationExceptionHandler as it gets triggered when an AuthenticationException is encountered
public class CustomAuthExceptionHandler implements AuthenticationEntryPoint, RequestCache {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final String message = "Unauthenticated User tried accessing %s, exception : %s".formatted(request.getRequestURI(), authException.getMessage());
        log.error(message);
        final Map<String, Object> body = new HashMap<>();
        body.put("error", authException.getClass().getSimpleName());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        objectMapper.writeValue(response.getOutputStream(), body);
    }


    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        log.info("Saving request for redirect");
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
        SimpleSavedRequest simpleSavedRequest = new SimpleSavedRequest();
        simpleSavedRequest.setMethod(request.getMethod());
        simpleSavedRequest.setRedirectUrl(request.getRequestURI());
        return simpleSavedRequest;
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        log.info("Removing request from the cache");
    }
}
