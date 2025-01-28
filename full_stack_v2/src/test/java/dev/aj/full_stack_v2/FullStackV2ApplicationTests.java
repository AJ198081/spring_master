package dev.aj.full_stack_v2;

import org.springframework.boot.SpringApplication;

class FullStackV2ApplicationTests {

    public static void main(String[] args) {
        SpringApplication.from(FullStackV2ApplicationTests::main)
                .withAdditionalProfiles("test")
                .with(PostgresTestContainerConfiguration.class)
                .run(args);
    }
}
