package dev.aj.full_stack_v5.auth.service.security.util;

import dev.aj.full_stack_v5.auth.domain.dtos.SecurityUser;
import dev.aj.full_stack_v5.auth.domain.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUtils {

    private final Environment environment;

    public String generateAccessToken(Authentication authentication) {

        SecurityUser securityUser = extractSecurityUser(authentication);

        Long jwtExpirationMillis = environment.getProperty("jwt.expiration.ms", Long.class, 100_000L);

        Date tokenExpiration = new Date();
        tokenExpiration.setTime(tokenExpiration.getTime() + jwtExpirationMillis);

        return Jwts.builder()
                .subject(securityUser.getUsername())
                .claim("id", securityUser.getId())
                .claim("roles", securityUser.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .issuedAt(new Date())
                .expiration(tokenExpiration)
                .signWith(getSigingKey())
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {

        SecurityUser securityUser = extractSecurityUser(authentication);

        Long refreshTokenExpirationMillis = environment.getProperty("jwt.refresh.expiration.ms", Long.class, 10_000_000L);

        Date refreshTokenExpiration = new Date();
        refreshTokenExpiration.setTime(refreshTokenExpiration.getTime() + refreshTokenExpirationMillis);

        return Jwts.builder()
                .subject(securityUser.getUsername())
                .issuedAt(new Date())
                .expiration(refreshTokenExpiration)
                .signWith(getSigingKey())
                .compact();
    }

    private static SecurityUser extractSecurityUser(Authentication authentication) {
        return Stream.ofNullable(authentication.getPrincipal())
                .filter(SecurityUser.class::isInstance)
                .map(SecurityUser.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unable to extract user '%s' from Authentication object".formatted(authentication.getPrincipal())));
    }

    public String getUsernameFromToken(String token) {
        return parseClaimsFromToken(token)
                .getSubject();
    }

    public boolean isJwtValid(@NonNull String token) {

        if (StringUtils.isBlank(token)) {
            return false;
        }

        try {
            Claims claims = parseClaimsFromToken(token);
            if (claims.getExpiration().before(new Date())) {
                log.error("Expired JWT token for user: {}", claims.getSubject());
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        }

        return false;
    }

    private Key getSigingKey() {
        String jwtSecretValue = environment.getProperty("jwt.secret", String.class);
        Objects.requireNonNull(jwtSecretValue, "jwt.secret must be set in application.properties");

        return Keys.hmacShaKeyFor(jwtSecretValue.getBytes());
    }

    private Claims parseClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
