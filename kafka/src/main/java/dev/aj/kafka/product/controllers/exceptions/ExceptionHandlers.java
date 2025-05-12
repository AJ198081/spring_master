package dev.aj.kafka.product.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setType(URI.create("https://google.com/search?q=%s".formatted(URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8))));
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setProperties(Map.of("timestamp", Instant.now()));

        return ResponseEntity.of(
                        Optional.of(ErrorResponse.builder(ex, problemDetail).build())
                );
    }

}
