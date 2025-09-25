package dev.aj.full_stack_v6.security.config.filters;

import dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions.JwtValidationException;
import dev.aj.full_stack_v6.security.utils.CookieUtils;
import dev.aj.full_stack_v6.security.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("AuthTokenFilter processing request for {}", request.getRequestURI());

        String jwt = jwtUtils.getJwt(request);

        if (jwt != null) {

            try {
                if (jwtUtils.validateJwt(jwt)) {
                    String username = jwtUtils.getUsernameFromJwt(jwt);
                    setSecurityContextHolderWithValidAuthentication(request, username);
                }
            } catch (JwtValidationException e) {
                log.warn("JWT validation failed for {}", request.getRequestURI());
                // Check if the request is an OPTIONS request, if so, return 200 OK
                if (request.getMethod().equals("OPTIONS")) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    // Otherwise, check if the refreshToken cookie is present, if so, try to refresh the token
                } else if (cookieUtils.requestContainsValidRefreshTokenCookie(request)) {
                    log.info("Fetching the refresh token from the request cookies");
                  String username = cookieUtils.getUsernameFromRefreshTokenCookie(request);
                  setSecurityContextHolderWithValidAuthentication(request, username);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                }
                return;
            }

        }
        filterChain.doFilter(request, response);
    }

    private void setSecurityContextHolderWithValidAuthentication(HttpServletRequest request, String username) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException ex) {
            log.warn("JWT subject user not found: {}. Proceeding without authentication.", username);
            SecurityContextHolder.clearContext();
        }
    }
}
