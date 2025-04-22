package dev.aj.redisson.controllers;

import dev.aj.redisson.services.FibonacciService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fibonacci")
@RequiredArgsConstructor
public class FibonacciController {

    private final FibonacciService fibonacciService;

    @RequestMapping("/{number}")
    public Mono<Long> getFibonacciNumber(@PathVariable int number) {
        return Mono.fromSupplier(() -> fibonacciService.getFibonacciNumber(number));
    }

    @GetMapping("/clear/{number}")
    public Mono<Void> clearCache(@PathVariable int number) {
        return Mono.fromRunnable(() -> fibonacciService.clearCacheForTheNumber(number));
    }

}
