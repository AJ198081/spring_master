package dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions;

public class UnauthorisedOperationException extends Throwable {

    public UnauthorisedOperationException(String message) {
        super(message);
    }
}
