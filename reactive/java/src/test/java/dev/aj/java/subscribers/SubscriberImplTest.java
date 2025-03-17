package dev.aj.java.subscribers;

import dev.aj.java.PostgresTCConfiguration;
import dev.aj.java.publishers.PublisherImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.util.Streamable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Import(PostgresTCConfiguration.class)
@Slf4j
class SubscriberImplTest {

    @Autowired
    private SubscriberImpl subscriber;

    @Autowired
    private PublisherImpl publisher;

    @org.junit.jupiter.api.Test
    void onNext() {
        publisher.subscribe(subscriber);
        subscriber.getSubscription().request(10);
    }

    @Test
    void monoPublisher() {
        Mono<String> emailPublisher = Mono.justOrEmpty(Optional.of("abg@gmail.com"));
        emailPublisher.subscribe(subscriber);
        subscriber.getSubscription().request(10);
    }

    @Test
    void monoPublisherConsumerSubscriber() {
        Mono<String> emailPublisher = Mono.justOrEmpty(Optional.of("abg@gmail.com"));
        emailPublisher.subscribe(email -> log.info("Email received: {}", email.replace("@", " @")));
    }

    @Test
    void monoPublisherConsumerSubscriberWithAdditionalImplementation() {

        Mono<String> emailPublisher = Mono.justOrEmpty(Optional.of("abg@gmail.com"))
                .map(email -> email.replace("@", ", and from domain "));

        emailPublisher.subscribe(
                email -> log.info("Email is received from user: {}", email.replace("@", " @")),
                err -> log.error("Error message: {}", err.getMessage()),
                () -> log.info("Completed"),
                subscription -> subscription.request(10)
        );
    }

    @Test
    void testMonoPublisher() {
        Mono<String> emailPublisher = Mono.justOrEmpty(Optional.of("abg@gmail.com"));
        emailPublisher.subscribe(email -> log.info("Email is received: {}", email.replace("@", " @")));
    }

    @Test
    void lazyInitialization() {
        List<? extends Number> ids = List.of(1, 2, 3, 4L);
        Mono<? extends Number> summationPublisher = Mono.justOrEmpty(sumOfNumbers(ids));
        summationPublisher.subscribe(sum -> log.info("Sum is: {}", sum));

        Mono<? extends Number> supplierPublisher = Mono.fromSupplier(() -> sumOfNumbers(ids).orElseThrow());

        List<? extends Number> list = Streamable
                .of(ids)
                .toList();

    }

    private Optional<? extends Number> sumOfNumbers(List<? extends Number> ids) {

        double sum = 0.0;
        for (Number id : ids) {
            sum = sum + id.doubleValue();
        }
        return sum == 0.0 ? Optional.empty() : Optional.of(sum);
    }


}
