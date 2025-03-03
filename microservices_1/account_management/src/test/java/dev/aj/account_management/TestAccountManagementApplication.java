package dev.aj.account_management;

import org.springframework.boot.SpringApplication;

class TestAccountManagementApplication {

    public static void main(String[] args) {
        SpringApplication.from(AccountManagementApplication::main)
                .with(PostgresTCConfig.class, TestConfig.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
