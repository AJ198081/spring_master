package dev.aj.talent_fulfillment;

import org.springframework.boot.SpringApplication;

class TestTalentFulfillmentApplication {

    public static void main(String[] args) {
        SpringApplication.from(TalentFulfillmentApplication::main)
                .with(PostgresTestContainer.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
