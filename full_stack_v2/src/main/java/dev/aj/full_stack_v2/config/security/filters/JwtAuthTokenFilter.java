package dev.aj.full_stack_v2.config.security.filters;

import dev.aj.full_stack_v2.config.security.jwt.JWTUtils;
import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import dev.aj.full_stack_v2.services.impl.SecurityUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthTokenFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final SecurityUserService securityUserService;

    /**
     * Filters incoming HTTP requests, validates JWT tokens to ensure user authentication.
     * If a valid JWT token is found, it extracts the user details from
     * {@link dev.aj.full_stack_v2.services.impl.SecurityUserService} and sets the security context.
     *
     * @param request     the HTTP request being processed, containing the JWT token in its header
     * @param response    the HTTP response being built for the client
     * @param filterChain the chain of filters to pass the request and response through
     * @throws ServletException if an application-specific error occurs during request processing
     * @throws IOException      if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.debug("JwtAuthTokenFilter called for URI: {}", request.getRequestURI());

        String jwtFromRequest = null;

        try {
            jwtFromRequest = jwtUtils.getJwtFromRequest(request);

            if (jwtFromRequest != null && jwtUtils.validateJwtToken(jwtFromRequest)) {
                String subjectUsername = jwtUtils.getUsernameFromJwtToken(jwtFromRequest);
                SecurityUser user = (SecurityUser) securityUserService.loadUserByUsername(subjectUsername);
                UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        /*
          Spring Security automatically populates `WebAuthenticationDetails` if the `WebAuthenticationDetailsSource` is used.
          It attaches this metadata to the `Authentication` object.
          @provides a standard way to extract request-specific details (like IP and session ID) from the `Authentication` object
         */
                usernamePasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthToken);
            }
        } catch (Exception e) {
            log.error("Exception: {} while parsing JWT: {}", e.getMessage(), jwtFromRequest);
        }

        filterChain.doFilter(request, response);

    }
}
