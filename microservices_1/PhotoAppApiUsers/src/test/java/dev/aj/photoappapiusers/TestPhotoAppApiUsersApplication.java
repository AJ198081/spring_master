package dev.aj.photoappapiusers;

import org.springframework.boot.SpringApplication;

class TestPhotoAppApiUsersApplication {

    public static void main(String[] args) {
        SpringApplication.from(PhotoAppApiUsersApplication::main)
                .with(PostgresTCConfig.class, TestConfig.class, InitDB.class, TestData.class, ActuatorConfig.class)
                .withAdditionalProfiles("test")
                .run(args);
    }

}
