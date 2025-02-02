package dev.aj.full_stack_v2.config.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLoggingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final ThreadLocal<Integer> invoked = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        invoked.set(invoked.get() == null ? 1 : invoked.get() + 1);

        log.info("Request {}: {}", invoked.get(), objectMapper.writeValueAsString(request.getRequestURL()));
        log.info("Response {}: {}", invoked.get(), objectMapper.writeValueAsString(response.getStatus()));
        filterChain.doFilter(request, response);
        log.info("After filter Response {}: {}", invoked.get(), objectMapper.writeValueAsString(response.getStatus()));
    }
}
