package dev.aj.reactive.exceptionHandlers;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ProblemDetail> handleDuplicateKeyException(DuplicateKeyException exception) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Duplicate Key");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setProperties(Map.of("field", exception.getMostSpecificCause().getMessage()));

        return Mono.just(problemDetail);
    }

}
