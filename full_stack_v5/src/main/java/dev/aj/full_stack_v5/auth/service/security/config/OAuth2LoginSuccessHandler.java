package dev.aj.full_stack_v5.auth.service.security.config;

import dev.aj.full_stack_v5.auth.domain.dtos.UserRegistrationDto;
import dev.aj.full_stack_v5.auth.domain.entities.Role;
import dev.aj.full_stack_v5.auth.domain.entities.User;
import dev.aj.full_stack_v5.auth.domain.mapper.UserMapper;
import dev.aj.full_stack_v5.auth.service.UserService;
import dev.aj.full_stack_v5.auth.service.security.util.CookieUtils;
import dev.aj.full_stack_v5.auth.service.security.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static dev.aj.full_stack_v5.auth.domain.enums.UserType.OAUTH;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final Environment environment;

    private String username;
    private String nameAttributeKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken && (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("github") || oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("google"))) {
                Map<String, Object> userAttributes = oAuth2AuthenticationToken.getPrincipal().getAttributes();

                if (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId().equals("github")) {
                    username = userAttributes.get("login").toString();
                    nameAttributeKey = "login";
                } else {
                    username = userAttributes.get("email").toString();
                    nameAttributeKey = "email";
                }
                userService.findUserByUsername(username)
                        .ifPresentOrElse(
                                user -> {
                                    if (CollectionUtils.isEmpty(user.getRoles())) {
                                        userService.addRoles(user, Set.of(UserMapper.ROLE_NAME_PREFIX.concat("USER")));
                                    }
                                    setAuthenticationContext(
                                            oAuth2AuthenticationToken,
                                            userAttributes,
                                            user);
                                },
                                () -> setAuthenticationContext(
                                        oAuth2AuthenticationToken,
                                        userAttributes,
                                        userService.registerNewUser(
                                                UserRegistrationDto.builder()
                                                        .username(username)
                                                        .roles(Set.of("ROLE_USER"))
                                                        .userType(OAUTH)
                                                        .build()
                                        ))
                        );
                String jwtToken = jwtUtils.generateAccessToken(SecurityContextHolder.getContext().getAuthentication());
                response.addHeader("Authorization", jwtToken);

                String refreshToken = jwtUtils.generateRefreshToken(SecurityContextHolder.getContext().getAuthentication());
                cookieUtils.addRefreshTokenCookie(response, refreshToken);

                String targetUrl = UriComponentsBuilder
                        .fromUriString(environment.getRequiredProperty("frontend.app.host").concat("/oauth2/callback"))
                        .queryParam("token", jwtToken)
                        .build()
                        .toUriString();

                this.setDefaultTargetUrl(targetUrl);

                super.onAuthenticationSuccess(request, response, authentication);
            }
    }

    private void setAuthenticationContext(OAuth2AuthenticationToken oAuth2AuthenticationToken, Map<String, Object> userAttributes, User registeredUser) {

        List<SimpleGrantedAuthority> userGrantedRoles = registeredUser.getRoles()
                .stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();

        DefaultOAuth2User defaultOAuth2User = new DefaultOAuth2User(
                userGrantedRoles,
                userAttributes,
                nameAttributeKey
        );

        OAuth2AuthenticationToken oAuth2Token = new OAuth2AuthenticationToken(
                defaultOAuth2User,
                userGrantedRoles,
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
        );

        SecurityContextHolder.getContext().setAuthentication(oAuth2Token);
    }
}
