package dev.aj.hibernate_jpa;

import com.github.javafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

@TestConfiguration
public class TestDataConfig {

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }
}
