package dev.aj.full_stack_v6_kafka;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    public RestClient restClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public Faker faker() {

        return new Faker(Locale.of("en", "IN"));
    }
}
