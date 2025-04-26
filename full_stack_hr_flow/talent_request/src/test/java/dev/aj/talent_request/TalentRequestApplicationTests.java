package dev.aj.talent_request;

import org.springframework.boot.SpringApplication;

class TalentRequestApplicationTests {

    public static void main(String[] args) {
        SpringApplication.from(TalentRequestApplication::main)
                .with(PostgresTestContainer.class)
                .withAdditionalProfiles("test")
                .run(args);
    }
}
