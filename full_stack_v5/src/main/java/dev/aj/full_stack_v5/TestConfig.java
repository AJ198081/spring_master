package dev.aj.full_stack_v5;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Locale;

@TestConfiguration( proxyBeanMethods = false)
public class TestConfig {

    public RestClient restClient(String baseUrl) {

        ByteArrayHttpMessageConverter byteArrayMessageConverter = new ByteArrayHttpMessageConverter();
        byteArrayMessageConverter.setSupportedMediaTypes(List.of(MediaType.ALL));

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        return RestClient.builder()
                .baseUrl(baseUrl)
                .messageConverters(List.of(byteArrayMessageConverter, jsonConverter))
//                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }
}
