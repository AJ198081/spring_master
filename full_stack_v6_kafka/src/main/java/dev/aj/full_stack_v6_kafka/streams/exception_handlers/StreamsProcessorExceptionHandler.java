package dev.aj.full_stack_v6_kafka.streams.exception_handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.errors.ErrorHandlerContext;
import org.apache.kafka.streams.errors.ProcessingExceptionHandler;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.processor.api.Record;

import java.util.Map;

@Slf4j
public class StreamsProcessorExceptionHandler implements ProcessingExceptionHandler {

    @Override
    public ProcessingHandlerResponse handle(ErrorHandlerContext context, Record<?, ?> record, Exception exception) {
        log.error("Error processing the stream failed due to {}", exception.getMessage());

        if (exception instanceof StreamsException streamsException) {
            log.error("Stream processing failed due to {}", streamsException.getCause().getMessage());

            // Replace Thread will keep retrying the exception in different threads, so only do it if you sure the error will eventually resolve.
            // Otherwise, shutdown the Client
            return ProcessingHandlerResponse.CONTINUE;
        }

        return ProcessingHandlerResponse.FAIL;
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
