package dev.aj.full_stack_v3.service.impl;

import dev.aj.full_stack_v3.domain.dto.SecurityUser;
import dev.aj.full_stack_v3.domain.dto.UserLoginRequest;
import dev.aj.full_stack_v3.domain.dto.UserLoginResponse;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import dev.aj.full_stack_v3.domain.entity.User;
import dev.aj.full_stack_v3.domain.mapper.UserMapper;
import dev.aj.full_stack_v3.repository.UserRepository;
import dev.aj.full_stack_v3.service.UserService;
import dev.aj.full_stack_v3.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest userRegistrationRequest) {

        return userMapper.userToUserRegistrationResponse(
                userRepository.save(
                        userMapper.userRegistrationRequestToUser(userRegistrationRequest)
                )
        );

    }

    @Override
    public UserRegistrationResponse getUserByUsername(String username) {

        return userMapper.userToUserRegistrationResponse(
                userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(username)))
        );
    }

    @Override
    public UserRegistrationResponse getUserByUsernameOrEmail(String usernameOrEmail) {
        return userMapper.userToUserRegistrationResponse(
                userRepository.findUserByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(usernameOrEmail)))
        );
    }

    @Override
    public UserLoginResponse loginUser(UserLoginRequest userLoginRequest) {

        User user = userRepository.findByUsername(userLoginRequest.getUsername()).orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(userLoginRequest.getUsername())));

        if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return UserLoginResponse.builder()
                .token(jwtUtils.generateTokenFromUser(user))
                .build();
    }

    @Override
    public UserLoginResponse loginUser(Authentication authentication) {
        return UserLoginResponse.builder()
                .token(jwtUtils.generateTokenFromUser((User) authentication.getPrincipal()))
                .build();
    }

    @Override
    public String generateRefreshTokenFromAuthentication(Authentication authentication ) {
        return jwtUtils.generateRefreshTokenFromUser((User) authentication.getPrincipal());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new SecurityUser(userRepository.findUserByUsernameOrEmail(username, username).orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username))));
    }

}
