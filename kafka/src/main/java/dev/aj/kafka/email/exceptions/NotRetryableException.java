package dev.aj.kafka.email.exceptions;

public class NotRetryableException extends RuntimeException{

    public NotRetryableException(Throwable cause) {
        super(cause);
    }

    public NotRetryableException(String message) {
        super(message);
    }
}
