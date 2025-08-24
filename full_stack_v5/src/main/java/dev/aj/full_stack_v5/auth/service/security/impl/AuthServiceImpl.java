package dev.aj.full_stack_v5.auth.service.security.impl;

import dev.aj.full_stack_v5.auth.service.AuthService;
import dev.aj.full_stack_v5.auth.service.security.util.CookieUtils;
import dev.aj.full_stack_v5.auth.service.security.util.JwtUtils;
import dev.aj.full_stack_v5.order.domain.entities.Customer;
import dev.aj.full_stack_v5.order.services.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final SecurityUserDetailsService securityUserDetailsService;
    private final CustomerService customerService;

    @Override
    public String authenticateUser(String username, String password, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        String accessToken;

        try {
            Customer customer = customerService.getCustomerByUsername(username);
            accessToken = jwtUtils.generateAccessToken(authentication, customer);
        } catch (EntityNotFoundException e) {
            accessToken = jwtUtils.generateAccessToken(authentication);
        }

        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        cookieUtils.addRefreshTokenCookie(response, refreshToken);

        log.info("Login is successful for user: {}, accessToken and refreshToken generated.", username);

        return accessToken;
    }

    @Override
    public String getAccessTokenFromRefreshToken(HttpServletRequest request) {

        String refreshToken = cookieUtils.getRefreshTokenCookie(request)
                .split("=")[1];

        if (refreshToken != null && jwtUtils.isJwtValid(refreshToken)) {
            String username = jwtUtils.getUsernameFromToken(refreshToken);
            Authentication authentication = new UsernamePasswordAuthenticationToken(securityUserDetailsService.loadUserByUsername(username), null);
            String accessToken = jwtUtils.generateAccessToken(authentication);
            log.info("Refresh token is valid for user: {}, accessToken generated.", username);
            return accessToken;
        } else {
            log.error("Refresh token is invalid for user: {}", refreshToken);
            throw new CredentialsExpiredException("Refresh token is invalid");
        }
    }

    @Override
    public void logout(HttpServletResponse response) {
        cookieUtils.deleteRefreshTokenCookie(response);
        log.info("Logout successful");
    }
}
