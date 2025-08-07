package dev.aj.reactive.config.security;

import dev.aj.reactive.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Objects;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String jwt = getJwtFromRequest(exchange);

        if (jwt != null) {
            validateJwt(jwt)
                    .map(isValid ->
                            isValid
                            ? authenticateAndContinue(jwt, exchange, chain)
                            : handleInvalidTokenException(exchange));
        }

        return chain.filter(exchange);
    }

    private Mono<Void> authenticateAndContinue(String jwt, ServerWebExchange exchange, WebFilterChain chain) {

        return Mono.justOrEmpty(extractTokenSubject(jwt))
                .flatMap(subject -> {
                    var authentication = new UsernamePasswordAuthenticationToken(subject, null, Collections.emptyList());
                    authentication.setAuthenticated(true);
                    return chain
                            .filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                });
    }

    private String extractTokenSubject(String jwt) {
        return jwtService.extractSubjectFromToken(jwt);
    }

    private Mono<Void> handleInvalidTokenException(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private String getJwtFromRequest(ServerWebExchange exchange) {
        String authorizationHeader = Objects.requireNonNullElse(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION), "");

        if (!authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader
                .replace("Bearer ", "");
    }

    private Mono<Boolean> validateJwt(String jwt) {
        if (jwt == null) {
            return Mono.just(false);
        }

        return jwtService.validateJwt(jwt);
    }
}
