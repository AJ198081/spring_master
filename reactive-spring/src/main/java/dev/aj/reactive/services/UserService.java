package dev.aj.reactive.services;

import dev.aj.reactive.domain.dtos.UserRequestDto;
import dev.aj.reactive.domain.dtos.UserResponseDto;
import dev.aj.reactive.domain.mappers.UserMapper;
import dev.aj.reactive.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Mono<UserResponseDto> createUser(Mono<UserRequestDto> userRequestDto) {
        return userRequestDto
                .mapNotNull(userMapper::toEntity)
                .flatMap(userRepository::save)
                .mapNotNull(userMapper::toDto);
    }

    public Mono<UserResponseDto> getUserById(Mono<UUID> id) {
        return id
                .flatMap(userRepository::findById)
                .mapNotNull(userMapper::toDto);
    }

    public Flux<UserResponseDto> getUsersPage(Pageable pageable) {
        return userRepository.findAllBy(pageable)
                .mapNotNull(userMapper::toDto);
    }

    public Mono<UserDetails> getUserByUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("read", "write", "delete")
                        .build()
                );
    }


    public Mono<Long> totalCount() {
        return userRepository.count();
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return getUserByUsername(username);
    }
}
