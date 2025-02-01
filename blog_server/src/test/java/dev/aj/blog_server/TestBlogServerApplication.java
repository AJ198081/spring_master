package dev.aj.blog_server;

import org.springframework.boot.SpringApplication;

class TestBlogServerApplication {

    public static void main(String[] args) {
        SpringApplication.from(BlogServerApplication::main)
                .with(TestConfig.class, PostgresTestContainerConfiguration.class)
//                .with(TestConfig.class, CorsConfigOverride.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
