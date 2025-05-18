package dev.aj.kafka.email.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RetryableException extends RuntimeException {

    public RetryableException(Throwable cause) {
        super(cause);
    }

    public RetryableException(String message) {
        super(message);
    }
}
