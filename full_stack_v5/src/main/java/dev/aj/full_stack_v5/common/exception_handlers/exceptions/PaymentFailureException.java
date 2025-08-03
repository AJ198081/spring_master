package dev.aj.full_stack_v5.common.exception_handlers.exceptions;

public class PaymentFailureException extends RuntimeException {

    public PaymentFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
