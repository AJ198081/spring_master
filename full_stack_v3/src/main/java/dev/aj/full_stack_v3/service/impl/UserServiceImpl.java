package dev.aj.full_stack_v3.service.impl;

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
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

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
    public UserLoginResponse loginUser(UserLoginRequest userLoginRequest) {

        User user = userRepository.findByUsername(userLoginRequest.getUsername()).orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(userLoginRequest.getUsername())));
        if (!user.getPassword().equals(userLoginRequest.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return UserLoginResponse.builder()
                .token(jwtUtils.generateTokeFromUser(user))
                .build();
    }
}
