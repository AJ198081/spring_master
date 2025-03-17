package dev.aj.java.subscribers;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.Duration;

@Slf4j
public class GenericSubscriber<T> implements Subscriber<T> {

    private final String name;

    public GenericSubscriber(String name) {
        this.name = name;
    }


    @Override
    public void onSubscribe(Subscription s) {
        log.info("Subscription received from {}", name);
        s.request(Long.MAX_VALUE);
    }


    @SneakyThrows
    @Override
    public void onNext(T t) {
        Thread.sleep(Duration.ofSeconds(1));
        log.info("Received: {}", t);
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
