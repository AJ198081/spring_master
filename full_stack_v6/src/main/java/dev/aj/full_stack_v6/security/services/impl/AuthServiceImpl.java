package dev.aj.full_stack_v6.security.services.impl;

import dev.aj.full_stack_v6.common.domain.dtos.LoginRequest;
import dev.aj.full_stack_v6.common.domain.dtos.LoginResponse;
import dev.aj.full_stack_v6.security.AuthService;
import dev.aj.full_stack_v6.security.utils.CookieUtils;
import dev.aj.full_stack_v6.security.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest loginRequest, jakarta.servlet.http.HttpServletResponse response) {

        Authentication authenticated = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));

        if (authenticated != null && authenticated.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authenticated.getPrincipal();
            String jwtToken = jwtUtils.generateJwt(userDetails);
            cookieUtils.addRefreshTokenCookieToResponse(response, userDetails.getUsername());
            return new LoginResponse(
                    jwtToken,
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
            );
        } else {
            log.error("Login failed for username: {}", loginRequest.username());
            throw new BadCredentialsException("Invalid username or password for user: %s".formatted(loginRequest.username()));
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        cookieUtils.invalidateRefreshTokenCookie(request, response);
    }
}
