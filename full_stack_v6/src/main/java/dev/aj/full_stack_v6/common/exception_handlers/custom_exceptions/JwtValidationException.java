package dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions;

public class JwtValidationException extends Throwable {
    public JwtValidationException(String jwtErrorMessage, Exception exception) {
        super(jwtErrorMessage, exception);
    }
}
