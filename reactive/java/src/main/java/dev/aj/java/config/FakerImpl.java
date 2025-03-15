package dev.aj.java.config;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.stream.Stream;

@Component
public class FakerImpl {

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }

    @Bean
    public Stream<String> generateRandomEmail(Faker faker) {
        return Stream.generate(() -> faker.internet().emailAddress());
    }


}
