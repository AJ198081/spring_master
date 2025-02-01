package dev.aj.blog_server;

import com.github.javafaker.Faker;
import dev.aj.blog_server.domain.entities.Comment;
import dev.aj.blog_server.domain.entities.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Locale;
import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {

    @Value("${server.port: -1}")
    private int port;

    @Bean
    @Order(1)
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:%d".formatted(port))
                .defaultHeader(HttpHeaders.ORIGIN, "http://localhost:%d".formatted(port + 10))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public Stream<Post> generateStreamOfPosts(Faker faker) {

        return Stream.generate(() -> Post.builder()
                .title(faker.lorem().characters(10, 50, true, true))
                .description(faker.lorem().paragraph())
                .build());
    }

    @Bean
    public Stream<Comment> generateStreamOfComments(Faker faker) {
        return Stream.generate(() -> Comment.builder()
                .text(faker.lorem().paragraph())
                .build());
    }


}
