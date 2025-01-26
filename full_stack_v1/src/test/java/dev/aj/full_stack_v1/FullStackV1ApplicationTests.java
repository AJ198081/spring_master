package dev.aj.full_stack_v1;

import org.springframework.boot.SpringApplication;

class TestFullStackV1Application {

    public static void main(String[] args) {
        SpringApplication.from(FullStackV1Application::main)
                .with(PostgresTestContainerConfiguration.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
