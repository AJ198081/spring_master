package dev.aj.java.publishers;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublisherImpl implements Publisher<String> {

    private final Subscription subscription;

    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        subscriber.onSubscribe(this.subscription);
    }
}
