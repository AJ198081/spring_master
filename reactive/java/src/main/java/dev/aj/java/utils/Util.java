package dev.aj.java.utils;

import dev.aj.java.subscribers.GenericSubscriber;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Mono;

public class Util {

    public static <T>Subscriber<T> subscriber(String name) {
        return new GenericSubscriber<>(name);
    }

    public static <T>Subscriber<T> subscriber() {
        return new GenericSubscriber<>("Default Subscriber");
    }

    public static void main(String[] args) {

        Mono<String> helloPublisher = Mono.just("Hello World");
        helloPublisher.subscribe(subscriber("Subscriber 0"));
        helloPublisher.subscribe(subscriber("Subscriber 1"));
        helloPublisher.subscribe(subscriber("Subscriber 2"));
        helloPublisher.subscribe(subscriber("Subscriber 3"));
        helloPublisher.subscribe(subscriber("Subscriber 4"));

    }

}
