package dev.aj.api_gateway.config.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PublicKey;
import java.util.Objects;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${jwt.secret:null}")
    private String jwtSigningKey;

    private final Environment environment;

    public AuthorizationHeaderFilter(Environment environment) {
        super(Config.class);
        this.environment = environment;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest httpRequest = exchange.getRequest();

            if (!httpRequest.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Gateway failed to detect Authorization header in the request", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = Objects.requireNonNull(httpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION), "Authorization header is null")
                    .stream()
                    .findFirst()
                    .orElseThrow();

            String jwt = authorizationHeader.replace("Bearer ", "");

            if (!isJwtValid(jwt)) {
                return onError(exchange, "Gateway failed to validate JWT", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        ServerHttpResponse httpResponse = exchange.getResponse();
        httpResponse.setStatusCode(httpStatus);
        httpResponse.getHeaders().add("x-error-message", errorMessage);
        return httpResponse.setComplete();
    }

    public static class Config {

    }

    private boolean isJwtValid(String jwt) {

        JwtParser jwtTokenParser = Jwts.parser().verifyWith((SecretKey) getKeyPair()).build();

        try {
            Jws<Claims> jwtClaims = jwtTokenParser.parseSignedClaims(jwt);

            Header header = jwtClaims.getHeader();
            Claims body = jwtClaims.getPayload();

            Long tokenExpiry = body.get("exp", Long.class);

            if (tokenExpiry < System.currentTimeMillis() / 1000) {
                return false;
            }

            String jwtSubject = body.get("sub", String.class);

            if (jwtSubject.isBlank()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private PublicKey publicKey() {
        return (PublicKey) getKeyPair();
    }

    private Key getKeyPair() {
        return Keys.hmacShaKeyFor(Encoders.BASE64.encode(jwtSigningKey.getBytes()).getBytes());
    }

}
