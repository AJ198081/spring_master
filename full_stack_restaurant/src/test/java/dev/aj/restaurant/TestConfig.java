package dev.aj.restaurant;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Locale;

@TestConfiguration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class TestConfig {

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("EN", "IND"));
    }


}
