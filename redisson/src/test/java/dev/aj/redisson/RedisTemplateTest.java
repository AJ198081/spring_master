package dev.aj.redisson;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisTemplateTest {

    @Autowired
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Autowired
    private RedissonReactiveClient redissonReactiveClient;

    @BeforeAll
    void beforeAll() {
        StepVerifier.create(reactiveStringRedisTemplate.delete(reactiveStringRedisTemplate.keys("*")))
                .consumeNextWith(count -> System.out.println("Deleted " + count + " keys"))
                .verifyComplete();
    }

    @RepeatedTest(value = 3, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("Reactive Redis Template Instantiates")
    void testReactiveRedisTemplateInstantiates() {

        ReactiveValueOperations<String, String> stringValueOps = reactiveStringRedisTemplate.opsForValue();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Flux<String> saturateRedis = Flux.range(1, 500_000)
                .flatMap(i -> stringValueOps.set("key-" + i, "value-" + i))
                .thenMany(stringValueOps.get("key-5"));

        StepVerifier.create(saturateRedis)
                .expectNext("value-5")
                .verifyComplete();

        stopWatch.stop();
        System.out.println("Time taken to saturate Redis: " + stopWatch.getDuration().toMillis() + " ms");
    }

    @RepeatedTest(value = 3, name = "{displayName} {currentRepetition}/{totalRepetitions}")
    @DisplayName("Saturate Redis with Reactive Redis Client")
    void testReactiveRedisTemplateInstantiatesWithCodec() {
        RAtomicLongReactive sharedAtomicLong = redissonReactiveClient.getAtomicLong("atomic-bucket");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
//        Mono<Void> setInitialValue = sharedAtomicLong.set(0);
        Flux<Long> saturateRedis = Flux.range(1, 500_000)
                .flatMap(i -> sharedAtomicLong.incrementAndGet())
                .thenMany(sharedAtomicLong.get());

        StepVerifier.create(saturateRedis)
                .consumeNextWith(number -> System.out.printf("Number received is %d%n", number))
                .verifyComplete();

        stopWatch.stop();
        System.out.println("Time taken to saturate Reactive Redis: " + stopWatch.getDuration().toMillis() + " ms");
    }
}
