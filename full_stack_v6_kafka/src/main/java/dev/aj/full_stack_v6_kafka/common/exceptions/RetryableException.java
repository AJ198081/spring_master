package dev.aj.full_stack_v6_kafka.common.exceptions;

public class RetryableException extends RuntimeException {

    @SuppressWarnings("unused")
    public RetryableException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public RetryableException(String message) {
        super(message);
    }

}
