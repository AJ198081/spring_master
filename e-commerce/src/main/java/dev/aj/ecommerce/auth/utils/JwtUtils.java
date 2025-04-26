package dev.aj.ecommerce.auth.utils;

import dev.aj.ecommerce.auth.domain.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private static final String JWT_AUTH_TOKEN_PREFIX = "Bearer ";
    private final Environment environment;

    @Value("${jwt.secret:${jwt.secret.key}}")
    private String jwtSecret;

    @Value("${jwt.expiration.ms:3600000}")
    private int jwtExpirationInMs;

    @Value("${jwt.refresh.expiration.ms:86400000}")
    private int refreshTokenExpirationInMs;

    public String getJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(JWT_AUTH_TOKEN_PREFIX)) {
            return authorizationHeader.substring(JWT_AUTH_TOKEN_PREFIX.length());
        }
        return null;
    }

    public String generateTokenFromUser(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(jwtExpirationInMs)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshTokenFromUser(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(refreshTokenExpirationInMs)))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateJwtTokenFromRefreshToken(String refreshToken) {
        String username = getUsernameFromJwtToken(refreshToken);
        if (username != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);

            return Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plusMillis(jwtExpirationInMs)))
                    .signWith(getSigningKey())
                    .compact();
        }
        return null;
    }

    public String getUsernameFromJwtToken(String jwtToken) {
        return getClaimFromToken(jwtToken, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateJwtToken(String jwt) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(jwt);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (JwtException e) {
            log.error("JWT token validation error: {}", e.getMessage());
        }
        return false;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromJwtToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}