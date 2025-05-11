package dev.aj.reactive;

import dev.aj.reactive.config.NettyServerConfig;
import org.springframework.boot.SpringApplication;

class TestReactiveSpringApplication {

    public static void main(String[] args) {
        SpringApplication.from(ReactiveSpringApplication::main)
                .with(TestConfig.class, TestData.class, InitDatabase.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
