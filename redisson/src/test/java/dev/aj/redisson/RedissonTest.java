package dev.aj.redisson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
public class RedissonTest {

    @Autowired
    private RedissonReactiveClient redissonReactiveClient;

    @Autowired
    private RedissonClient redissonClient;

    @AfterEach
    public void cleanup() {
        // Clean up all keys after each test
        StepVerifier.create(redissonReactiveClient.getKeys().flushall()).verifyComplete();
        redissonClient.getKeys().flushall();
    }

    @Test
    public void testBasicSetAndGet() {
        RBucketReactive<String> testBucket = redissonReactiveClient.getBucket("test-key", StringCodec.INSTANCE);
        Mono<Void> set = testBucket.set("test-value");
        Mono<String> get = testBucket.get();

        StepVerifier.create(set.then(get))
                .expectNext("test-value")
                .verifyComplete();
    }

    @Test
    public void testStudentObjectPersistence() {

        RBucketReactive<Student> studentBucket = redissonReactiveClient.getBucket("student-key");

        Mono<Void> set = studentBucket.set(new Student(15, "John", 25));
        Mono<Student> get = studentBucket.get();

        StepVerifier.create(set.then(get))
                .expectNextMatches(student -> student.name().equals("John") && student.age() == 25)
                .verifyComplete();
    }

    @Test
    public void testKeyExpiry() {
        RBucketReactive<String> expiryBucket = redissonReactiveClient.getBucket("expiry-key", StringCodec.INSTANCE);

        // Set value with 2 second expiry
        Mono<Void> setWithExpiry = expiryBucket.set("expiring-value", Duration.ofSeconds(2))
                .then();

        // Execute the set operation
        StepVerifier.create(setWithExpiry)
                .verifyComplete();

        // Verify key exists immediately after setting
        StepVerifier.create(expiryBucket.isExists())
                .expectNext(true)
                .verifyComplete();

        // Wait for 3 seconds (longer than expiry time)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verify key no longer exists
        StepVerifier.create(expiryBucket.isExists())
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testKeyExpiryEvent() {

        // Before starting the test ensure you are listening to the expiry event, in Redis-Cli -> CONFIG GET notify-keyspace-events
        RBucketReactive<String> expiryBucket = redissonReactiveClient.getBucket("expiry-key", StringCodec.INSTANCE);

        // Set value with 2 second expiry
        Mono<Void> setWithExpiry = expiryBucket.set("expiring-value", Duration.ofSeconds(2))
                .then();

        Mono<Void> expiryListener = expiryBucket.addListener(new ExpiredObjectListener() {
            @Override
            public void onExpired(String name) {
                System.out.printf("Key %s expired%n", name);
            }
        }).then();

        StepVerifier.create(setWithExpiry.then(expiryListener))
                .verifyComplete();

        try {
            Thread.sleep(Duration.ofSeconds(3));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testKeyExpiration() {

        RBucketReactive<String> expiryBucket = redissonReactiveClient.getBucket("expiry-key", StringCodec.INSTANCE);
        Mono<Void> setWithExpiry = expiryBucket.set("expiring-value", Duration.ofSeconds(10));

        StepVerifier.create(setWithExpiry)
                .verifyComplete();

        StepVerifier.create(expiryBucket.isExists())
                .expectNext(true)
                .verifyComplete();

        // The two MONOs will complete in sequence, first returns true when key is expired, the second returns false as the key doesn't exist.
        StepVerifier.create(expiryBucket.expire(Instant.now()).concatWith(expiryBucket.isExists()))
                .expectNext(true)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testMultipleValues() {
        RBucketReactive<String> bucket1 = redissonReactiveClient.getBucket("multi-key-1", StringCodec.INSTANCE);
        RBucketReactive<String> bucket2 = redissonReactiveClient.getBucket("multi-key-2", StringCodec.INSTANCE);
        RBucketReactive<String> bucket3 = redissonReactiveClient.getBucket("multi-key-3", StringCodec.INSTANCE);

        // Set multiple values
        Mono<Void> setAll = bucket1.set("value-1")
                .then(bucket2.set("value-2"))
                .then(bucket3.set("value-3"));

        // Get all keys matching pattern
        Mono<List<String>> getAllKeys = setAll
                .then(redissonReactiveClient.getKeys().getKeysByPattern("multi-key-*").collectList());

        // Verify all keys were created
        StepVerifier.create(getAllKeys)
                .expectNextMatches(keys -> keys.size() == 3 &&
                        keys.contains("multi-key-1") &&
                        keys.contains("multi-key-2") &&
                        keys.contains("multi-key-3"))
                .verifyComplete();
    }

    @Test
    public void testHashOperations() {
        RMapReactive<String, String> hashMap = redissonReactiveClient.getMap("test-hash", StringCodec.INSTANCE);

        // Add multiple entries to hash using fastPut which returns a boolean
        StepVerifier.create(hashMap.fastPut("field1", "value1"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(hashMap.fastPut("field2", "value2"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(hashMap.fastPut("field3", "value3"))
                .expectNext(true)
                .verifyComplete();

        // Get all entries and verify
        Mono<Map<String, String>> getAll = hashMap.readAllMap();

        StepVerifier.create(getAll)
                .expectNextMatches(map ->
                        map.size() == 3 &&
                                "value1".equals(map.get("field1")) &&
                                "value2".equals(map.get("field2")) &&
                                "value3".equals(map.get("field3")))
                .verifyComplete();

        // Test getting a single field
        StepVerifier.create(hashMap.get("field2"))
                .expectNext("value2")
                .verifyComplete();
    }

    @Test
    public void testMapOperations() {
        TypedJsonJacksonCodec studentCodec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapReactive<Integer, Student> students = redissonReactiveClient.getMap("students", studentCodec);

        StepVerifier.create(students.fastPut(1, new Student(1, "Jake", 29)))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(students.fastPut(2, new Student(2, "Russell", 30)))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(students.fastPut(3, new Student(3, "Jane", 28)))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(students.readAllMap())
                .expectNextMatches(map -> map.size() == 3 && map.containsKey(1) && map.containsKey(2) && map.containsKey(3))
                .verifyComplete();

    }

    @Test
    public void testListOperations() {
        RListReactive<String> list = redissonReactiveClient.getList("test-list", StringCodec.INSTANCE);

        // Add items to the list
        StepVerifier.create(list.add("item1"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(list.add("item2"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(list.add("item3"))
                .expectNext(true)
                .verifyComplete();

        // Get all items and verify
        Mono<List<String>> getAll = list.readAll();

        StepVerifier.create(getAll)
                .expectNextMatches(items ->
                        items.size() == 3 &&
                                "item1".equals(items.get(0)) &&
                                "item2".equals(items.get(1)) &&
                                "item3".equals(items.get(2)))
                .verifyComplete();

        // Test getting an item by index
        StepVerifier.create(list.get(1))
                .expectNext("item2")
                .verifyComplete();
    }

    @Test
    public void testSetOperations() {
        RSetReactive<String> set = redissonReactiveClient.getSet("test-set", StringCodec.INSTANCE);

        // Add items to set
        StepVerifier.create(set.add("item1"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(set.add("item2"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(set.add("item3"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(set.add("item1")) // Duplicate item should be ignored
                .expectNext(false)
                .verifyComplete();

        // Verify set size
        StepVerifier.create(set.size())
                .expectNext(3)
                .verifyComplete();

        // Test contains operations for each item
        StepVerifier.create(set.contains("item1"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(set.contains("item2"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(set.contains("item3"))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(set.contains("item4"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    public void testAtomicOperations() {
        RAtomicLongReactive atomicLong = redissonReactiveClient.getAtomicLong("test-atomic");

        // Set initial value
        Mono<Void> setInitial = atomicLong.set(10);

        // Perform atomic operations
        Mono<Long> incrementAndGet = setInitial.then(atomicLong.incrementAndGet());

        StepVerifier.create(incrementAndGet)
                .expectNext(11L)
                .verifyComplete();

        // Perform another atomic operation
        StepVerifier.create(atomicLong.addAndGet(5))
                .expectNext(16L)
                .verifyComplete();
    }

    @Test
    public void testAtomicDoubleOperations() {
        RAtomicDoubleReactive atomicDouble = redissonReactiveClient.getAtomicDouble("test-atomic-double");
        Mono<Void> setInitial = atomicDouble.set(10.5);

        Mono<Double> incrementedValue = setInitial.then(atomicDouble.incrementAndGet());

        StepVerifier.create(incrementedValue)
                .expectNext(11.5)
                .verifyComplete();

        StepVerifier.create(atomicDouble.addAndGet(0.5))
                .expectNext(12.0)
                .verifyComplete();
    }

    @Test
    public void testLocalCachedMap() {

        RLocalCachedMap<Integer, Student> studentCacheMap = getIntegerStudentRLocalCachedMap();

        studentCacheMap.put(0, new Student(0, "Janet", 23));
        studentCacheMap.put(1, new Student(1, "Jake", 29));
        studentCacheMap.put(2, new Student(2, "Russell", 30));
        studentCacheMap.put(3, new Student(3, "Jane", 28));

        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(tick -> {
                    if (studentCacheMap.containsKey(tick.intValue())) {
                        System.out.printf("Current: %s%n%n", studentCacheMap.get(tick.intValue()));
                    } else {
                        System.out.printf("Default: %s%n%n", studentCacheMap.get(0));
                    }
                })
                .subscribe();
        try {
            Thread.sleep(Duration.ofSeconds(2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testLocalCachedMapWithCodec() {
        RLocalCachedMap<Integer, Student> studentCacheMap = getIntegerStudentRLocalCachedMap();
        studentCacheMap.put(0, new Student(0, "Rosemary", 34));
    }

    @Test
    public void testPubSubImplementationUsingRedisson() {
        RTopicReactive jwtSecretTopic = redissonReactiveClient.getTopic("jwt-secret", StringCodec.INSTANCE);

        Mono<Long> initialJwtSecret = jwtSecretTopic.publish("jwt-secret-value");

        // Redis-CLI -> PUBLISH jwt-secret "Rest is history - 1"
        jwtSecretTopic.getMessages(String.class)
                .doOnNext(message -> System.out.printf("Received message: %s%n", message))
                .doOnError(error -> System.out.printf("Error: %s%n", error.getMessage()))
                .subscribe();

        jwtSecretTopic.addListener(String.class, new MessageListener<String>() {
                    @Override
                    public void onMessage(CharSequence channel, String message) {
                        System.out.printf("Received from the topic: %s, message: %s%n", channel, message);
                    }
                })
                .doOnError(error -> System.out.printf("Error: %s%n", error.getMessage()))
                .doOnNext(listener -> System.out.println("Listener registered"))
                .subscribe();

        StepVerifier.create(initialJwtSecret)
                .expectNext(1L)
                .verifyComplete();

        try {
            Thread.sleep(Duration.ofSeconds(2));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Done");
    }

    @Test
    public void testBatchPublishingRedissonReactive() {
        RBatchReactive reactiveClientBatch = redissonReactiveClient.createBatch(BatchOptions.defaults()
                .executionMode(BatchOptions.ExecutionMode.IN_MEMORY)
                .responseTimeout(10, TimeUnit.SECONDS)
                .retryAttempts(3));

        RListReactive<Object> numbersReactiveList = reactiveClientBatch.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Object> numbersReactiveSet = reactiveClientBatch.getSet("numbers-set", LongCodec.INSTANCE);

        IntStream.range(0, 500_000).forEach(i -> {
            numbersReactiveList.add(i);
            numbersReactiveSet.add(i);
        });

        StepVerifier.create(reactiveClientBatch.execute().then())
                .verifyComplete();
    }

    @Test
    public void nonTransactionalBucketTest() {
        RBucketReactive<Long> bucket1 = redissonReactiveClient.getBucket("test-bucket-1", LongCodec.INSTANCE);
        RBucketReactive<Long> bucket2 = redissonReactiveClient.getBucket("test-bucket-2", LongCodec.INSTANCE);

        Mono<Void> setAccountValues = bucket1.set(50L)
                .then(bucket2.set(50L));

        StepVerifier.create(setAccountValues).verifyComplete();

        transfer(bucket1, bucket2, 30).thenReturn(0)
                .map(amount -> amount == 0 ? "Transfer successful" : "Transfer failed")
                .map(message -> message.isBlank() ? "Error" : 5 / 0)
                .doOnError(error -> System.out.printf("Error: %s%n", error.getMessage()))
                .doOnNext(message -> System.out.printf("Message: %s%n", message))
                .subscribe();

        try {
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Flux.zip(bucket1.get(), bucket2.get())
                .subscribe(t -> System.out.printf("Account 1 balance: %d, Account 2 balance: %d%n", t.getT1(), t.getT2()));
    }

    @Test
    public void transactionalBucketTest() {

        RTransactionReactive transaction = redissonReactiveClient.createTransaction(TransactionOptions.defaults());
        RBucketReactive<Long> bucket1 = transaction.getBucket("test-bucket-3", LongCodec.INSTANCE);
        RBucketReactive<Long> bucket2 = transaction.getBucket("test-bucket-4", LongCodec.INSTANCE);

        Mono<Void> setAccountValues = bucket1.set(50L)
                .then(bucket2.set(50L));

        StepVerifier.create(setAccountValues
                        .then(transfer(bucket1, bucket2, 30))
                        .thenReturn(0)
                        .map(i -> 5 / i)
                        .then(transaction.commit())
                        .doOnError(error -> System.out.printf("Error: %s%n", error.getMessage()))
                        .onErrorResume(error -> transaction.rollback())
                )
                .verifyComplete();
    }

    private Mono<Void> transfer(RBucketReactive<Long> from, RBucketReactive<Long> to, int amount) {
        return Flux.zip(from.get(), to.get()) // Zips both buckets into a single stream
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> from.set(t.getT1() - amount).thenReturn(t))
                .flatMap(t -> to.set(t.getT1() + amount))
                .then(); // Wait for all transfers to complete
    }


    private RLocalCachedMap<Integer, Student> getIntegerStudentRLocalCachedMap() {
        // Get local cached map options
        LocalCachedMapOptions<Integer, Student> mapCacheOptions = LocalCachedMapOptions.<Integer, Student>name("students")
                .codec(new TypedJsonJacksonCodec(Integer.class, Student.class))
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE); // Try docker compose down here

        // Get the Local Cache, implemented using Redisson
        return redissonClient.getLocalCachedMap(mapCacheOptions);
    }

    record Student(Integer id, String name, int age) implements Serializable {
    }
}
