package dev.aj.photoappapiusers.services;

import dev.aj.photoappapiusers.domain.dto.UserRegistrationRequestDto;
import dev.aj.photoappapiusers.domain.dto.UserRegistrationResponseDto;
import dev.aj.photoappapiusers.domain.entity.User;

public interface UserService {
    UserRegistrationResponseDto saveUser(UserRegistrationRequestDto userRegistrationRequestDto);

    User findUserByUsernameOrEmail(String username);
}
