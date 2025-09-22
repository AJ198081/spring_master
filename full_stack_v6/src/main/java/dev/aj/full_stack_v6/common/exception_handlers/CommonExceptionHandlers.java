package dev.aj.full_stack_v6.common.exception_handlers;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CommonExceptionHandlers {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleEntityNotFoundException(EntityNotFoundException ex) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;

        ProblemDetail entityNotFoundProblemDetail = ProblemDetail.forStatusAndDetail(
                notFound,
                ex.getMessage()
        );

        return ResponseEntity
                .status(notFound)
                .body(entityNotFoundProblemDetail);
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    public ResponseEntity<ProblemDetail> handleEntityExistsException(EntityExistsException ex) {
        HttpStatus conflictStatus = HttpStatus.CONFLICT;
        ProblemDetail conflictProblemDetail = ProblemDetail.forStatusAndDetail(
                conflictStatus,
                ex.getMessage()
        );

        return ResponseEntity
                .status(conflictStatus)
                .body(conflictProblemDetail);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex) {

        HttpStatus conflictStatus = HttpStatus.CONFLICT;

        ProblemDetail badRequestProblemDetail = ProblemDetail.forStatusAndDetail(
                conflictStatus,
                ex.getMessage()
        );

        return ResponseEntity
                .status(conflictStatus)
                .body(badRequestProblemDetail);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        HttpStatus badRequestStatus = HttpStatus.BAD_REQUEST;
        ProblemDetail badRequestProblemDetail = ProblemDetail.forStatusAndDetail(
                badRequestStatus,
                ex.getBody().getDetail());

        badRequestProblemDetail.setProperties(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getObjectName() + "." + fieldError.getField(),
                        (fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(), "Invalid error value")),
                        (oldValue, newValue) -> oldValue + "; " + newValue)
                ));

        return ResponseEntity
                .status(badRequestStatus)
                .body(badRequestProblemDetail);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex) {
        HttpStatus conflictStatus = HttpStatus.BAD_REQUEST;
        ProblemDetail badRequestProblemDetail = ProblemDetail.forStatus(conflictStatus);

        badRequestProblemDetail.setProperty("message", ex.getMessage());
        return ResponseEntity
                .status(conflictStatus)
                .body(badRequestProblemDetail);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex) {
        HttpStatus unauthorizedStatus = HttpStatus.UNAUTHORIZED;
        ProblemDetail badRequestProblemDetail = ProblemDetail.forStatus(unauthorizedStatus);
        badRequestProblemDetail.setProperty("message", ex.getMessage());

        return ResponseEntity
                .status(unauthorizedStatus)
                .body(badRequestProblemDetail);
    }

    // catch all, needed because I can't use spring's problem.details.enabled property
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {

        HttpStatus status = ex instanceof ResponseStatusException responseStatusException
                ? HttpStatus.valueOf(responseStatusException.getStatusCode().value())
                : HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(problemDetail);
    }
}
