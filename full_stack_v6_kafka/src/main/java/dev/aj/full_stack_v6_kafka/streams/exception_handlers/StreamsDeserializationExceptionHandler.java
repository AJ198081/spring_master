package dev.aj.full_stack_v6_kafka.streams.exception_handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ErrorHandlerContext;

import java.util.Map;

@Slf4j
public class StreamsDeserializationExceptionHandler implements DeserializationExceptionHandler {

    private int errorCount = 0;


    @Override
    public DeserializationHandlerResponse handle(ErrorHandlerContext context, ConsumerRecord<byte[], byte[]> record, Exception exception) {

        log.error("Error deserializing record: {}, %n failed due to {}", record, exception.getMessage());

        log.error("Error count: {}", errorCount);

        if (errorCount < 2) {
            errorCount++;
            return DeserializationHandlerResponse.CONTINUE;
        }

        return DeserializationHandlerResponse.FAIL;
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
