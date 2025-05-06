package dev.aj.reactive.services;

import dev.aj.reactive.domain.model.User;
import dev.aj.reactive.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ReactiveAuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public Mono<Map<String, String>> authenticate(String username, String password) {

        return authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .then(userRepository.findByEmail(username))
                .map(this::toMap);
    }

    private Map<String, String> toMap(User user) {
        return Map.of("id", user.getId().toString(), "email", user.getEmail());
    }


}
