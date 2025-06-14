package dev.aj.full_stack_v5.common.domain.exceptionHandlers;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ProblemDetail> handleEntityAlreadyExists(EntityExistsException e) {

        ProblemDetail entityConflictDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(entityConflictDetail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException e) {

        ProblemDetail badRequestDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(badRequestDetail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(EntityNotFoundException e) {
        ProblemDetail entityNotFoundDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(entityNotFoundDetail);
    }



}
