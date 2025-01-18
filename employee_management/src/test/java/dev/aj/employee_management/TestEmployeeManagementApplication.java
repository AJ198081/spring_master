package dev.aj.employee_management;

import org.springframework.boot.SpringApplication;

class TestEmployeeManagementApplication {

    public static void main(String[] args) {
        SpringApplication
                .from(EmployeeManagementApplication::main)
                .with(PostgresTestContainerConfiguration.class, TestDataConfig.class, InitDatabase.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
