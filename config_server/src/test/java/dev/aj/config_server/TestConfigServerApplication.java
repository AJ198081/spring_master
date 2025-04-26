package dev.aj.config_server;

import org.springframework.boot.SpringApplication;

class TestConfigServerApplication {


    public static void main(String[] args) {
        SpringApplication.from(ConfigServerApplication::main)
                .withAdditionalProfiles("test")
                .run(args);
    }


}
