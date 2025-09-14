package dev.aj.full_stack_v6.common.exception_handlers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
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

}
