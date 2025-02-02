package dev.aj.full_stack_v2.config.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserAgentFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String browser = request.getHeader(HttpHeaders.USER_AGENT);
        log.info("User Agent: {}", browser);
        if (browser.contains("Mozilla")) {
            response.sendError(HttpStatus.CONFLICT.value(), "Invalid User Agent");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
