package dev.aj.hibernate_jpa;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@TestConfiguration
public class TestDataConfig {

    @Value( "${server.port: 80}")
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        builder.baseUrl(String.format("http://localhost:%d/%s", serverPort, contextPath))
                .defaultHeader("content-type", "application/json")
                .defaultHeader("accept", "application/json");

        return builder.build();
    }
}
