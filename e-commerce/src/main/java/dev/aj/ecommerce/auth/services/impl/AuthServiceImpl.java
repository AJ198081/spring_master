package dev.aj.ecommerce.auth.services.impl;

import dev.aj.ecommerce.auth.domain.dtos.AuthResponseDto;
import dev.aj.ecommerce.auth.domain.dtos.UserLoginDto;
import dev.aj.ecommerce.auth.domain.dtos.UserRegistrationDto;
import dev.aj.ecommerce.auth.domain.entities.User;
import dev.aj.ecommerce.auth.domain.mappers.RegistrationUserMapper;
import dev.aj.ecommerce.auth.repositories.AuthRepository;
import dev.aj.ecommerce.auth.services.AuthService;
import dev.aj.ecommerce.auth.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMapCache;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final RegistrationUserMapper registrationUserMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RMapCache<String, String> refreshTokenCache;

    @Override
    @Transactional
    public UserRegistrationDto register(UserRegistrationDto userRegistrationDto) {
        User savedUser = authRepository.save(registrationUserMapper.toEntity(userRegistrationDto));
        return registrationUserMapper.toDto(savedUser);
    }

    @Override
    public AuthResponseDto login(UserLoginDto userLoginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDto.getUsernameOrEmail(),
                        userLoginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        User user = authRepository.findByUsernameOrEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtils.generateTokenFromUser(user);
        String refreshToken = jwtUtils.generateRefreshTokenFromUser(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponseDto refreshToken(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtils.getUsernameFromJwtToken(refreshToken);

        if (refreshTokenCache.get(username) == null) {
            throw new RuntimeException("Refresh token is expired");
        }

        User user = authRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtils.generateTokenFromUser(user);

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken) // Return the same refresh token
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
    }
}
