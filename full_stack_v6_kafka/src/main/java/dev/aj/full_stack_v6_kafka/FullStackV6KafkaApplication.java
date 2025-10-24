package dev.aj.full_stack_v6_kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@Modulithic(
        sharedModules = {
                "common",
                "config"
        }
)
public class FullStackV6KafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FullStackV6KafkaApplication.class, args);
    }

}
