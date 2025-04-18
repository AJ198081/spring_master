package dev.aj.ecommerce;

import org.springframework.boot.SpringApplication;

class TestECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.from(ECommerceApplication::main)
                .with(PostgresTCConfig.class,
                        TestConfig.class,
                        InitDatabase.class,
                        CORSOverride.class,
                        TestData.class)
                .withAdditionalProfiles("test", "compose")
                .run(args);
    }

}
