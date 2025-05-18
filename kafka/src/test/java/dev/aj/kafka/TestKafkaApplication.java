package dev.aj.kafka;

import org.springframework.boot.SpringApplication;

class TestKafkaApplication {

    public static void main(String[] args) {

        SpringApplication.from(KafkaApplication::main)
                .with(TestConfig.class, TestData.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
