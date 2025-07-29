package dev.aj.full_stack_v5.auth.service.security.filter;

import dev.aj.full_stack_v5.auth.domain.dtos.SecurityUser;
import dev.aj.full_stack_v5.auth.service.security.impl.SecurityUserDetailsService;
import dev.aj.full_stack_v5.auth.service.security.util.CookieUtils;
import dev.aj.full_stack_v5.auth.service.security.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
@NullMarked
public class AuthTokenFilter extends OncePerRequestFilter {

    private final SecurityUserDetailsService securityUserDetailsService;
    private final JwtUtils jwtUtils;
    private final Environment environment;
    private final CookieUtils cookieUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt = parseJwt(request);

        if (Objects.nonNull(jwt)) {
            log.info("Validating JWT token");
            if (jwtUtils.isJwtValid(jwt)) {
                String userFromToken = jwtUtils.getUsernameFromToken(jwt);
                SecurityUser securityUser = securityUserDetailsService.loadUserByUsername(userFromToken);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                log.error("JWT token is invalid");
                String refreshTokenCookie = cookieUtils.getRefreshTokenCookie(request);
                if (refreshTokenCookie != null && jwtUtils.isJwtValid(refreshTokenCookie)) {
                    log.info("Refresh token cookie has valid jwt, generating new access token");
                    String usernameFromToken = jwtUtils.getUsernameFromToken(refreshTokenCookie);
                    SecurityUser securityUser = securityUserDetailsService.loadUserByUsername(usernameFromToken);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    String accessToken = jwtUtils.generateAccessToken(SecurityContextHolder.getContext().getAuthentication());
                    response.addHeader("Authorization", accessToken);
                }

            }
        }

        filterChain.doFilter(request, response);
    }

    private @Nullable String parseJwt(HttpServletRequest request) {

        String jwtHeaderName = environment.getProperty("jwt.header", String.class, "Authorization");
        String jwtHeaderValuePrefix = environment.getProperty("authorization.token.header.value.prefix", String.class, "Bearer")
                .concat(" ");
        String authorizationHeader = request.getHeader(jwtHeaderName);

        if (authorizationHeader != null && authorizationHeader.startsWith(jwtHeaderValuePrefix)) {
            return authorizationHeader.substring(jwtHeaderValuePrefix.length());
        }
        return null;
    }
}
