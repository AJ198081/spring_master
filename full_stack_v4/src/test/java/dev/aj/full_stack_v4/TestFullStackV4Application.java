package dev.aj.full_stack_v4;

import org.springframework.boot.SpringApplication;

class TestFullStackV4Application {

    public static void main(String[] args) {
        SpringApplication.from(FullStackV4Application::main)
                .with(
                        CORSOverride.class,
                        TestData.class,
                        PhotosFactory.class,
                        InitDatabase.class,
                        TestConfig.class,
                        PhotoController.class
                )
                .withAdditionalProfiles("test")
                .run(args);
    }
}
