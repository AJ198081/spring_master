package dev.aj.full_stack_v3.config.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v3.domain.dto.SecurityUser;
import dev.aj.full_stack_v3.domain.dto.UserLoginResponse;
import dev.aj.full_stack_v3.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String JWT_AUTH_TOKEN_PREFIX = "Bearer ";
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwtFromRequest = getJwtFromRequest(request);

        String refreshToken = getRefreshTokenFromRequest(request);

        try {
            if (jwtFromRequest != null && jwtUtils.validateJwtToken(jwtFromRequest)) {
                SecurityUser securityUser = jwtUtils.getSecurityUserFromToken(jwtFromRequest);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (request.getRequestURI().contains("/refresh-token") && jwtUtils.validateJwtToken(refreshToken)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                String jwtTokenResponse = objectMapper.writeValueAsString(UserLoginResponse.builder()
                        .token(jwtUtils.generateJwtTokenFromRefreshToken(refreshToken))
                        .build());

                ServletOutputStream outputStream = response.getOutputStream();

                outputStream.write(jwtTokenResponse.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }
        } catch (Exception e) {
            throw new AccessDeniedException("JWT Token has expired");
        }

        filterChain.doFilter(request, response);
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        return cookies == null ? null : Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(JWT_AUTH_TOKEN_PREFIX)) {
            return authorizationHeader.substring(JWT_AUTH_TOKEN_PREFIX.length());
        }
        return null;
    }
}
