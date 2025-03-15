package dev.aj.java.subscribers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
public class SubscriberImpl implements Subscriber<String> {

    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
    }

    @Override
    public void onNext(String email) {
        log.info("Email received: {}", email);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error: {}", t.getMessage());
    }

    @Override
    public void onComplete() {
        log.info("Done!");
    }
}
