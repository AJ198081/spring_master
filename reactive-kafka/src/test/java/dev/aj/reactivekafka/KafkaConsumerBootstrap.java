package dev.aj.reactivekafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Bean;
import reactor.core.Disposable;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@TestComponent
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerBootstrap {

    private final KafkaConfiguration kafkaConfiguration;

    @Bean
    public Disposable testKafkaConfiguration() {
        return KafkaReceiver.create(
                        ReceiverOptions.create(kafkaConfiguration.getConsumerProperties())
                                .subscription(List.of("order-events"))
                )
                .receive()
                .doOnNext(record ->
                        log.info("Received: Key - {}, Value - {}", record.key(), record.value()))
                .doOnNext(record -> record.receiverOffset().acknowledge())
                .doOnComplete(() -> log.info("Completed"))
                .doOnError(error -> log.error("Error: {}", error.getMessage()))
                .subscribe();
    }

}
