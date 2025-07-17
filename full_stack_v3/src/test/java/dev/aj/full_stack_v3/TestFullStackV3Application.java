package dev.aj.full_stack_v3;

import org.springframework.boot.SpringApplication;

class TestFullStackV3Application {

    public static void main(String[] args) {
        SpringApplication.from(FullStackV3Application::main)
                .with(
                        TestConfig.class,
                        CORSOverride.class,
                        InitDatabase.class,
                        TestData.class)
                .withAdditionalProfiles("test", "compose")
                .run(args);
    }
}
