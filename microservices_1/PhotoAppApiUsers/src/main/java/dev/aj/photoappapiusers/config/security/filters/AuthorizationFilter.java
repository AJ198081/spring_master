package dev.aj.photoappapiusers.config.security.filters;

import dev.aj.photoappapiusers.domain.dto.SecurityUser;
import dev.aj.photoappapiusers.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    private final JwtUtils jwtUtils;

    public AuthorizationFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authTokenHeader == null || !authTokenHeader.startsWith(BEARER_TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(authTokenHeader.substring(BEARER_TOKEN_PREFIX.length()));
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String jwtToken) {
        if (!jwtUtils.validateJwtToken(jwtToken)) {
            return null;
        }

        SecurityUser securityUser = jwtUtils.getSecurityUserFromToken(jwtToken);

        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }
}
