package dev.aj.elasticsearch;

import org.springframework.boot.SpringApplication;


class TestElasticsearchApplication {

    public static void main(String[] args) {
        SpringApplication.from(ElasticsearchApplication::main)
                .with(TestConfig.class, TestData.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
