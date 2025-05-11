package dev.aj.reactive.exceptionHandlers;

import dev.aj.reactive.controllers.RegistrationController;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.yaml.snakeyaml.util.UriEncoder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

// The Golden rule is to have a single exception handler for each exception,
// For this rule to be effective, don't handle exceptions in any of the other layers
@RestControllerAdvice(basePackageClasses = RegistrationController.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ErrorResponse> handleDuplicateKeyException(DuplicateKeyException exception) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setDetail(exception.getMostSpecificCause().getMessage());
        problemDetail.setProperties(Map.of("field", exception.getMostSpecificCause().getMessage()));

        return Mono.just(ErrorResponse.builder(exception, problemDetail).build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ErrorResponse> handleException(Exception exception) {
        return Mono.just(ErrorResponse
                .builder(exception, ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()))
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return Mono.just(ErrorResponse
                .builder(exception, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()))
                .build()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ErrorResponse> handleBadCredentialsException(BadCredentialsException exception) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Caught and thrown => ".concat(exception.getMessage()));
        problemDetail.setTitle("Unauthorized User");
        problemDetail.setProperties(Map.of("timestamp", Instant.now()));
        problemDetail.setType(URI.create("https://google.com/search?q=%s".formatted(UriEncoder.encode(exception.getMessage()))));

        return Mono.just(ErrorResponse
                .builder(exception, problemDetail)
                .build()
        );
    }


    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ErrorResponse> handleBeanDefinitionValidationException(WebExchangeBindException exception) {

        String validationErrors = exception.getBindingResult().getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return Mono.just(ErrorResponse
                .builder(exception, ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, validationErrors))
                .build()
        );
    }

}
