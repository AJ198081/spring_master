package dev.aj.full_stack_v6.security.utils.impl;

import dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions.JwtValidationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * <a href="https://github.com/jwtk/jjwt">Official guidance on JWTs for Java</a>
 */

@Component
@RequiredArgsConstructor
@Slf4j
class JwtUtils implements dev.aj.full_stack_v6.security.utils.JwtUtils {

    private final Environment environment;

    @Override
    public @Nullable String getJwt(HttpServletRequest request) {
        String authorizationTokenHeaderName = environment.getProperty("authorization.token.header.name", "Authorization");

        String bearerToken = request.getHeader(authorizationTokenHeaderName);

        if (bearerToken != null) {
            String authTokenPrefix = environment.getProperty("authorization.token.header.value.prefix", "Bearer ");
            if (bearerToken.startsWith(authTokenPrefix)) {
                return bearerToken.substring(authTokenPrefix.length());
            }
        }
        return null;
    }

    @Override
    public String generateJwt(UserDetails userDetails) {

        log.info("Generating JWT for username: {}", userDetails.getUsername());

        List<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", authorities)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now()
                        .plusMillis(environment.getRequiredProperty("jwt.expiration.ms", Long.class))))
                .signWith(getSecretKey())
                .compact();
    }

    @Override
    public String getUsernameFromJwt(String jwt) {

        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    @Override
    public boolean validateJwt(String jwt) throws JwtValidationException {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException exception) {
            log.error("Exception whilst validating the JWT: {}", exception.getMessage());
            throw new JwtValidationException("Invalid JWT token", exception);
        }
    }

    @Override
    public String generateRefreshJwt(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now()
                        .plusMillis(environment.getRequiredProperty("jwt.refresh.expiration.ms", Long.class))))
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
//        return Jwts.SIG.HS256.key().build();

        // If you saved the JWT Secret to BASE64 encoded string, you will then need to Decoders.BASE64.decode(jwtSecret)
        String jwtSecret = environment.getRequiredProperty("jwt.secret");
        byte[] jwtSecretBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(jwtSecretBytes);
    }
}
