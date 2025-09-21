package dev.aj.full_stack_v6.security.config.entry_points;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.savedrequest.SimpleSavedRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AuthEntryPointJwt implements AuthenticationEntryPoint, RequestCache {

    private static final String SPRING_SECURITY_REQUEST_CACHE = "SPRING_SECURITY_REQUEST_CACHE";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, String> parameters = new LinkedHashMap<>();
        response.setStatus(status.value());

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
