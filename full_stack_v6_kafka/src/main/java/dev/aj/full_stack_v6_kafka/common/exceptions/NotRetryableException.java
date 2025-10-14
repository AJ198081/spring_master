package dev.aj.full_stack_v6_kafka.common.exceptions;

public class NotRetryableException extends RuntimeException {

    @SuppressWarnings("unused")
    public NotRetryableException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public NotRetryableException(String message) {
        super(message);
    }

}
