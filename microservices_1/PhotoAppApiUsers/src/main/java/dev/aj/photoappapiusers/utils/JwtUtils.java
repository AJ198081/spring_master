package dev.aj.photoappapiusers.utils;

import dev.aj.photoappapiusers.domain.dto.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

        private static final String JWT_AUTH_TOKEN_PREFIX = "Bearer ";

        @Value("${jwt.secret: null}")
        private String jwtSecret;

        @Value("${jwt.expiration.ms: 3600000}")
        private int jwtExpirationInMs;

        public String getJwtFromRequest(HttpServletRequest request) {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith(JWT_AUTH_TOKEN_PREFIX)) {
                return authorizationHeader.substring(JWT_AUTH_TOKEN_PREFIX.length());
            }
            return null;
        }

        public String generateTokenFromUser(SecurityUser user) {
            return Jwts.builder()
                    .subject(user.getUsername())
                    .claim("roles", user.getAuthorities())
                    .claim("username", user.getUsername())
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plusMillis(jwtExpirationInMs)))
                    .signWith(key())
                    .compact();
        }

        public SecurityUser getSecurityUserFromToken(String jwtToken) {

            Claims payload = Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();
            return getUserFromJwtClaims(payload);
        }

    private SecurityUser getUserFromJwtClaims(Claims payload) {
        return SecurityUser.builder()
                .username(payload.getSubject())
                .role(payload.get("roles", String.class))
                .build();
    }


    private Key key() {
//        byte[] base64DecodedSecret = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(Encoders.BASE64.encode(jwtSecret.getBytes()).getBytes());
        }

        public String getUsernameFromJwtToken(String jwtToken) {
            return Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload()
                    .getSubject();
        }


        public boolean validateJwtToken(String jwt) {

            try {
                Jwts.parser()
                        .verifyWith((SecretKey) key())
                        .build().parseSignedClaims(jwt);
                return true;
            } catch (MalformedJwtException e) {
                log.error("Invalid JWT Token: {}", jwt);
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired: {}, expiration: {}", jwt, e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported JWT Token: {}, issue: {}", jwt, e.getMessage());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Unable to parse JWT Token: %s".formatted(jwt));
            } catch (JwtException e) {
                log.error("Token error for JWT: {}, error message: {}", jwt, e.getMessage());
            }
            return false;
        }
}
