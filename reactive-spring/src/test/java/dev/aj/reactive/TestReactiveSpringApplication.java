package dev.aj.reactive;

import org.springframework.boot.SpringApplication;

class TestReactiveSpringApplication {

    public static void main(String[] args) {
        SpringApplication.from(ReactiveSpringApplication::main)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
