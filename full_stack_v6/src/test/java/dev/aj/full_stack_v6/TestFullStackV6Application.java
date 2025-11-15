package dev.aj.full_stack_v6;

import org.springframework.boot.SpringApplication;

class TestFullStackV6Application {

    public static void main(String[] args) {

        SpringApplication.from(FullStackV6Application::main)
                .withAdditionalProfiles("test", "sqlLogging")
                .run(args);
    }

}
