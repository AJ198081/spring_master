package dev.aj.full_stack_v3.config.security.handlers;

import dev.aj.full_stack_v3.service.UserService;
import dev.aj.full_stack_v3.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    private final Environment environment;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        if (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("github")) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            String username = principal.getAttribute("login");
            String email = principal.getAttribute("email");

        }

    }
}
