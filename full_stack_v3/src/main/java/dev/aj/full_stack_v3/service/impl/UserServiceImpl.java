package dev.aj.full_stack_v3.service.impl;

import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationResponse;
import dev.aj.full_stack_v3.domain.mapper.UserMapper;
import dev.aj.full_stack_v3.repository.UserRepository;
import dev.aj.full_stack_v3.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

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
}
