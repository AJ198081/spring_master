package dev.aj.ecommerce.auth.services;

import dev.aj.ecommerce.auth.domain.dtos.AuthResponseDto;
import dev.aj.ecommerce.auth.domain.dtos.UserLoginDto;
import dev.aj.ecommerce.auth.domain.dtos.UserRegistrationDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    UserRegistrationDto register(UserRegistrationDto userRegistrationDto);

    AuthResponseDto login(UserLoginDto userLoginDto);

    AuthResponseDto refreshToken(String refreshToken);

    void logout(HttpServletRequest request);
}
