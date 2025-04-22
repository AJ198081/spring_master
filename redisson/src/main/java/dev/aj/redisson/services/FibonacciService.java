package dev.aj.redisson.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FibonacciService {

    // By default, Spring will cache the results of the method for 'X?' seconds, using all the method arguments as the cache key
    @Cacheable(value = "fibonacci", key = "#n")
    public long getFibonacciNumber(int n) {
        log.info("Calculating Fibonacci number for n = {}", n);
        return calculateFibonacciOfTheNumber(n);
    }

    private long calculateFibonacciOfTheNumber(int n) {
        // Fibonacci is recursive because each number is the sum of the previous two numbers
        // For example: F(5) needs F(4) + F(3), F(4) needs F(3) + F(2), and so on
        // The recursion stops when n <= 1, which are the base cases: F(0) = 0, F(1) = 1
        if (n <= 1) {
            return n;
        }

        // Using Spring's @Cacheable annotation to automatically cache results,
        // This prevents recalculating the same Fibonacci numbers multiple times
        return calculateFibonacciOfTheNumber(n - 1) + calculateFibonacciOfTheNumber(n - 2);
    }

    @CacheEvict(value = "fibonacci", key = "#number")
    public void clearCacheForTheNumber(int number) {
        log.info("Clearing cache for Fibonacci number {}", number);
    }

    @Scheduled(fixedRateString = "${redisson.cache.eviction.duration.ms}", timeUnit = TimeUnit.SECONDS)
    @CacheEvict(value = "fibonacci", allEntries = true)
    public void clearAllCache() {
        log.info("Clearing all Fibonacci cache entries");
    }
}
