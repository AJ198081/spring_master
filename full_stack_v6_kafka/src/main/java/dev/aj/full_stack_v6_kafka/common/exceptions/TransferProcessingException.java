package dev.aj.full_stack_v6_kafka.common.exceptions;

public class TransferProcessingException extends Throwable {
    public TransferProcessingException(String reason) {
        super(reason);
    }
}
