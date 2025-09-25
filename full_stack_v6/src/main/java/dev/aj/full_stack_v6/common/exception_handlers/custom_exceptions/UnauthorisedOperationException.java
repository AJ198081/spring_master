package dev.aj.full_stack_v6.common.exception_handlers.custom_exceptions;

import io.jsonwebtoken.JwtException;

public class UnauthorisedOperationException extends Throwable {

      public UnauthorisedOperationException(String message, JwtException exception) {
        super(message, exception);
    }

    public UnauthorisedOperationException(String message) {
        super(message);
    }
}
