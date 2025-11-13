package dev.aj.full_stack_v6_kafka.streams.exception_handlers;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.ErrorHandlerContext;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;

import java.util.Map;

@Slf4j
public class StreamsSerializationExceptionHandler implements ProductionExceptionHandler {
    @Override
    public void configure(Map<String, ?> configs) {

    }

    @Override
    public ProductionExceptionHandlerResponse handle(ErrorHandlerContext context, ProducerRecord<byte[], byte[]> record, Exception exception) {

        log.error("Error serializing record: {}, %n failed due to {}", record, exception.getMessage());
        return ProductionExceptionHandlerResponse.CONTINUE;
    }

}
