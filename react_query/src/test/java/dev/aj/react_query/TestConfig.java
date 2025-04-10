package dev.aj.react_query;

import com.github.javafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    public RestClient restClient(String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ORIGIN, "http://localhost:3000")
                .build();
    }

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }

}
