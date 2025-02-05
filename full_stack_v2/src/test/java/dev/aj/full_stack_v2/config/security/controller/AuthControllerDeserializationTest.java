package dev.aj.full_stack_v2.config.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v2.config.security.jwt.JWTUtils;
import dev.aj.full_stack_v2.domain.entities.security.SecurityUser;
import dev.aj.full_stack_v2.services.impl.SecurityUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@WebMvcTest(AuthController.class)
//@Import({ CustomDeserializers.class, GrantedAuthorityDeserializer.class, GrantedAuthorityDeserializer.class})
class AuthControllerDeserializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private JWTUtils jwtUtils;
    @MockitoBean private AuthenticationManager authenticationManager;
    @MockitoBean private SecurityUserService securityUserService;
    @MockitoBean private PasswordEncoder passwordEncoder;


    @Test
    void deserializeLoginRequest() throws Exception {
        SecurityUser securityUser = SecurityUser.builder()
                .username("test")
                .password("<PASSWORD>")
                .authorities(List.of("ROLE_USER", "ROLE_ADMIN"))
                .build();

        String serializedUser = objectMapper.writeValueAsString(securityUser);

        SecurityUser deserializedUser = objectMapper.readValue(serializedUser, SecurityUser.class);

    }

}