package dev.aj.full_stack_v5;

import net.datafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Locale;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    public RestClient restClient(String baseUrl) {

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON));


        ResourceHttpMessageConverter resourceConverter = new ResourceHttpMessageConverter();

        ByteArrayHttpMessageConverter byteArrayConverter = new ByteArrayHttpMessageConverter();
        byteArrayConverter.setSupportedMediaTypes(
                List.of(
                        MediaType.APPLICATION_OCTET_STREAM,
                        MediaType.IMAGE_PNG,
                        MediaType.MULTIPART_FORM_DATA,
                        MediaType.IMAGE_JPEG
                ));

        FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
        formConverter.setSupportedMediaTypes(List.of(MediaType.MULTIPART_FORM_DATA));

        MultipartHttpMessageWriter multipartHttpMessageWriter = new MultipartHttpMessageWriter();
        formConverter.setPartConverters(List.of(byteArrayConverter, resourceConverter));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .messageConverters(List.of(byteArrayConverter, jsonConverter, resourceConverter, formConverter))
                .build();
    }

    @Bean
    public Faker faker() {

        return new Faker(Locale.of("en", "IND"));
    }
}
