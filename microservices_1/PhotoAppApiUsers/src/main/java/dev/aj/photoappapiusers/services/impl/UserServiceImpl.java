package dev.aj.photoappapiusers.services.impl;

import dev.aj.photoappapiusers.domain.dto.UserRegistrationRequestDto;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationResponseDto;
import dev.aj.photoappapiusers.domain.entity.User;
import dev.aj.photoappapiusers.domain.mappers.UserMapper;
import dev.aj.photoappapiusers.repositories.UserRepository;
import dev.aj.photoappapiusers.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserRegistrationResponseDto saveUser(UserRegistrationRequestDto userRegistrationRequestDto) {
        return userMapper.entityToDto(
                userRepository.save(
                        userMapper.dtoToEntity(userRegistrationRequestDto)
                )
        );
    }

    @Override
    public User findUserByUsernameOrEmail(String username) {
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new NoSuchElementException("User '%s' not found".formatted(username)));
    }

}
