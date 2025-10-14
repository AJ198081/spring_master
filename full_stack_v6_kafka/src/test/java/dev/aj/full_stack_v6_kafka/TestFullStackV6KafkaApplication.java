package dev.aj.full_stack_v6_kafka;

import org.springframework.boot.SpringApplication;

public class TestFullStackV6KafkaApplication {

    static void main(String[] args) {

        SpringApplication.from(FullStackV6KafkaApplication::main)
                .with(BootstrapTopics.class)
                .withAdditionalProfiles("test")
                .run(args);
    }
}
