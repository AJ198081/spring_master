package dev.aj.java.publishers;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionImpl implements Subscription {

    private static final int MAX_ITEMS = 8;

    private final Subscriber<String> subscriber;
    private boolean isCancelled = false;
    private final Faker faker;


    @SneakyThrows
    @Override
    public void request(long n) {
        if (isCancelled) {
            return;
        }

        log.info("Subscriber requested {} items", n);

        for (int i = 0; i < n; i++) {
            if(i > MAX_ITEMS) break;
            subscriber.onNext("Email #%d [%s]".formatted(i, faker.internet().emailAddress()));
            Thread.sleep(Duration.ofSeconds(1));
        }
    }

    @Override
    public void cancel() {
        log.info("Subscription cancelled");
        this.isCancelled = true;
    }
}
