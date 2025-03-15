package dev.aj.java.subscribers;

import dev.aj.java.PostgresTCConfiguration;
import dev.aj.java.publishers.PublisherImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresTCConfiguration.class)
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

}