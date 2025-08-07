package dev.aj.reactive.services.impl;

import dev.aj.reactive.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final Environment environment;

    @Override
    public String generateToken(String subject) {
        return Jwts.builder()
                .header()
                .keyId("reactive_key")
                .and()
                .subject(subject)
                .issuedAt(new Date(Instant.now().toEpochMilli()))
                .expiration(new Date(Instant.now().toEpochMilli() + 3600_000))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public Mono<Boolean> validateJwt(String jwt) {

        Mono<Boolean> booleanMono = Mono.just(jwt)
                .map(this::parseClaims)
                .map(claims -> !claims.getExpiration().before(new Date()))
                .onErrorReturn(false);

        return booleanMono;
    }

    @Override
    public String extractSubjectFromToken(String jwt) {
        return parseClaims(jwt).getSubject();
    }

    private SecretKey getSigningKey() {

        return Optional.ofNullable(environment.getProperty("token.secret.key"))
                .map(String::getBytes)
                .map(Keys::hmacShaKeyFor)
                .orElseThrow(() -> new MissingResourceException("Missing the secret key", String.class.getName(), "token.secret.key"));
    }

    private Claims parseClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

}
