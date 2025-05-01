package dev.aj.reactive;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.client.HttpClient;

import java.util.Locale;
import java.util.Random;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    private HttpClient httpClient;

    @Bean
    public Faker faker() {
        // Passing the Secure Random ensures repeatable test data
        return new Faker(Locale.of("en", "IND"), new Random(290L));
    }

    public HttpClient httpClient(String host, int port) {
        if (httpClient == null) {
            httpClient = HttpClient.create();
            httpClient.baseUrl("http://%s:%s".formatted(host, port));
            httpClient.headers(headerBuilder -> headerBuilder.add("Content-Type", "application/json"));
            httpClient.headers(headerBuilder -> headerBuilder.add("Accept", "application/json"));
            httpClient.headers(headerBuilder -> headerBuilder.add("Accept-Language", "en-AU"));
            httpClient.headers(headerBuilder -> headerBuilder.add("Accept-Charset", "utf-8"));
        }
        return httpClient;
    }


}
