package dev.aj.full_stack_v6.security.controllers;

import dev.aj.full_stack_v6.common.domain.dtos.LoginDto;
import dev.aj.full_stack_v6.security.utils.CookieUtils;
import dev.aj.full_stack_v6.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserDetailsManager userDetailsManager;
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;

    @PostMapping("/login")
    public void login(@RequestBody LoginDto loginDto) {

        UserDetails userDetails = userDetailsManager.loadUserByUsername(loginDto.username());


    }
}
