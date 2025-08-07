package dev.aj.reactive.services;

import reactor.core.publisher.Mono;

public interface JwtService {
    String generateToken(String subject);

    Mono<Boolean> validateJwt(String jwt);

    String extractSubjectFromToken(String jwt);
}
