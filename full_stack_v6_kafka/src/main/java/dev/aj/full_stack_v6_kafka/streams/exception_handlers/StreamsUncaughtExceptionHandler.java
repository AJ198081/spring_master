package dev.aj.full_stack_v6_kafka.streams.exception_handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.StreamsException;

@Slf4j
public class StreamsUncaughtExceptionHandler implements org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler {
     @Override
    public StreamThreadExceptionResponse handle(Throwable exception) {

        log.error("Error processing the stream, failed due to {}", exception.getMessage());

        if (exception instanceof StreamsException streamsException) {
            log.error("Stream processing failed due to {}", streamsException.getCause().getMessage());

            // Replace Thread will keep retrying the exception in different threads, so only do it if you sure the error will eventually resolve.
            // Otherwise, shutdown the Client
            return StreamThreadExceptionResponse.REPLACE_THREAD;
        }

        return StreamThreadExceptionResponse.SHUTDOWN_APPLICATION;
    }
}
