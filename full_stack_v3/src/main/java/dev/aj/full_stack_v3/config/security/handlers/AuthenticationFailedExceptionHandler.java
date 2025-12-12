package dev.aj.full_stack_v3.config.security.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFailedExceptionHandler implements AuthenticationEntryPoint, RequestCache {

    private static final String SPRING_SECURITY_REQUEST_CACHE = "SPRING_SECURITY_REQUEST_CACHE";
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

        log.error("Unauthenticated user detected: {}", authException.getMessage());

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, authException.getMessage());
        problemDetail.setType(URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status/401"));
        problemDetail.setTitle("Unauthenticated");
        problemDetail.setInstance(URI.create("/login"));
        problemDetail.setDetail(authException.getMessage());
        problemDetail.setProperty("path", request.getRequestURI());
        problemDetail.setProperty("servletPath", request.getServletPath());

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            objectMapper.writeValue(response.getOutputStream(), problemDetail);
        } catch (Exception e) {
            log.error("Error writing unauthorized response: {}", e.getMessage());
        }

    }

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        SimpleSavedRequest simpleSavedRequest = new SimpleSavedRequest();
        simpleSavedRequest.setMethod(request.getMethod());
        simpleSavedRequest.setRedirectUrl(request.getRequestURI());
        simpleSavedRequest.setCookies(List.of(request.getCookies()));

        request.getSession().setAttribute(SPRING_SECURITY_REQUEST_CACHE, simpleSavedRequest);
    }

    @Override
    public SavedRequest getRequest(HttpServletRequest currentRequest, HttpServletResponse response) {
        HttpSession session = currentRequest.getSession(false);
		return (session != null) ? (SavedRequest) session.getAttribute(SPRING_SECURITY_REQUEST_CACHE) : null;
    }

    @Override
    public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().removeAttribute(SPRING_SECURITY_REQUEST_CACHE);
    }
}
