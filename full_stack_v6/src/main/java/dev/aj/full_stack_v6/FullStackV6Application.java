package dev.aj.full_stack_v6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

@SpringBootApplication
@Modulithic(
        sharedModules = {
                "security",
                "common",
                "email"
        },
        useFullyQualifiedModuleNames = false
)
public class FullStackV6Application {

    public static void main(String[] args) {

        SpringApplication.run(FullStackV6Application.class, args);
    }

}
