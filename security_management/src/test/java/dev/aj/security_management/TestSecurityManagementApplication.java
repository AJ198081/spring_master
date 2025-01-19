package dev.aj.security_management;

import org.springframework.boot.SpringApplication;

class TestSecurityManagementApplication {

    public static void main(String[] args) {
        SpringApplication
                .from(SecurityManagementApplication::main)
                .with(PostgresTestContainerConfiguration.class, TestDataConfig.class, InitDatabase.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
