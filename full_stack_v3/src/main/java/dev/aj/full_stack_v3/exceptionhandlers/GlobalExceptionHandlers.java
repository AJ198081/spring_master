package dev.aj.full_stack_v3.exceptionhandlers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandlers {

    public static final String ERROR_DETAIL_PREFIX = "Detail: ";

    @ExceptionHandler({NoSuchElementException.class, UsernameNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(Exception exception) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setType(URI.create("/login"));

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problemDetail.setTitle("Validation Error");

        problemDetail.setDetail(exception.getBindingResult()
                .getAllErrors().stream()
                .map(error -> {
                    FieldError fieldErrorDetail = (FieldError) error;
                    return String.format("%nValue '%s' for field '%s' is rejected because %s %n",
                            fieldErrorDetail.getRejectedValue(),
                            fieldErrorDetail.getField(),
                            error.getDefaultMessage()
                    );
                })
                .collect(Collectors.joining("----"))
        );

        return problemDetail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail dataIntegrityViolationExceptionHandler(DataIntegrityViolationException exception) {

        String message = exception.getMostSpecificCause().getLocalizedMessage();
        String errorMessage = message.substring(message.indexOf(ERROR_DETAIL_PREFIX) + ERROR_DETAIL_PREFIX.length());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(errorMessage);
        problemDetail.setType(URI.create("/login"));

        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail catchAllExceptionHandler(Exception exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(exception.getCause().getMessage());
        problemDetail.setType(URI.create("/login"));

        return problemDetail;
    }

}
