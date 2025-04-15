package dev.aj.react_query;

import dev.aj.react_query.init.InitialiseDatabase;
import dev.aj.react_query.init.TestData;
import org.springframework.boot.SpringApplication;


class TestReactQueryApplication {

    public static void main(String[] args) {
        SpringApplication.from(ReactQueryApplication::main)
                .with(PostgresTCConfig.class,
                        CORSOverride.class,
                        TestConfig.class,
                        TestData.class,
                        InitialiseDatabase.class
                )
                .withAdditionalProfiles("test")
                .run(args);
    }
}
