package dev.aj.full_stack_v5.auth.service.security.impl;

import dev.aj.full_stack_v5.auth.domain.dtos.SecurityUser;
import dev.aj.full_stack_v5.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public SecurityUser loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);

        return new SecurityUser(userService.getUserByTheUsername(username));
    }

}
