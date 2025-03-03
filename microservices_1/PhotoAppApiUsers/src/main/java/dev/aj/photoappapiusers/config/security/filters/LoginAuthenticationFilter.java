package dev.aj.photoappapiusers.config.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.photoappapiusers.domain.dto.SecurityUser;
import dev.aj.photoappapiusers.domain.dto.UserLoginRequestDto;
import dev.aj.photoappapiusers.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        UserLoginRequestDto userLoginRequestDto = null;

        try {
            userLoginRequestDto = objectMapper.readValue(request.getInputStream(), UserLoginRequestDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequestDto.getUsernameOrEmail(),
                        userLoginRequestDto.getPassword(),
                        new ArrayList<>())
        );

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        String jwtToken = jwtUtils.generateTokenFromUser((SecurityUser) authResult.getPrincipal());

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jwtToken);
        response.getWriter().flush();
    }
}
