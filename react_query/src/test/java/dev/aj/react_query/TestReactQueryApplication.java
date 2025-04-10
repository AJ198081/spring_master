package dev.aj.react_query;

import org.springframework.boot.SpringApplication;


class TestReactQueryApplication {

    public static void main(String[] args) {
        SpringApplication.from(ReactQueryApplication::main)
                .with(PostgresTCConfig.class,
                        TestConfig.class,
                        InitDatabase.class,
                        TestData.class)
                .withAdditionalProfiles("test")
                .run(args);
    }
}
