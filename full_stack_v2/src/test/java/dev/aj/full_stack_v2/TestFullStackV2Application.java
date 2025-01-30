package dev.aj.full_stack_v2;

import org.springframework.boot.SpringApplication;

class TestFullStackV2Application {

    public static void main(String[] args) {
        SpringApplication.from(FullStackV2Application::main)
                .withAdditionalProfiles("test")
                .with(PostgresTestContainerConfiguration.class, TestSecurityConfig.class)
                .run(args);
    }
}
