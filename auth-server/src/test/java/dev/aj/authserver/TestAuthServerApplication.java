package dev.aj.authserver;


import org.springframework.boot.SpringApplication;

class TestAuthServerApplication {

    public static void main(String[] args) {

        SpringApplication.from(AuthServerApplication::main)
                .withAdditionalProfiles("test")
                .run(args);
    }
}
