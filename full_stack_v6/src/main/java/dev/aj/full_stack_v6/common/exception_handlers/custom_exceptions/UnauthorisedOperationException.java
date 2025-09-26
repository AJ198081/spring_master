package dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions;

public class UnauthorisedOperationException extends RuntimeException {

    public UnauthorisedOperationException(String message) {
        super(message);
    }
}
