package dev.aj.full_stack_v5.common.exception_handlers;

import dev.aj.full_stack_v5.common.exception_handlers.exceptions.PaymentFailureException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.net.URI;

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
        entityNotFoundDetail.setTitle("User Entity Not Found");
        entityNotFoundDetail.setType(URI.create("https://support.google.com/accounts/thread/258682975/account-not-found-username-is-taken?hl=en"));

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(entityNotFoundDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthenticationException(AuthenticationException e) {
        ProblemDetail authNProblem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        authNProblem.setTitle("Authentication/Authorization Error");
        authNProblem.setProperty("error", """
                Authentication/Authorization Error occurred.
                    Please check your credentials and try again.
                        If the problem persists, please contact the system administrator.
                """);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(authNProblem);
    }

    @ExceptionHandler(PaymentFailureException.class)
    public ResponseEntity<ProblemDetail> handleException(PaymentFailureException e) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, e.getMessage());
        problemDetail.setTitle("Payment processing error, with code %s".formatted(e.getCause().getMessage()));
        problemDetail.setProperty("error", """
                An unexpected error occurred whilst processing your payment.
                    Please use a different card or contact your bank.
                """);
        problemDetail.setType(URI.create("https://docs.stripe.com/error-codes"));

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(problemDetail);
    }
}
