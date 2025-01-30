package dev.aj.full_stack_v2;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v2.domain.entities.Note;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Locale;
import java.util.stream.Stream;

@TestConfiguration(proxyBeanMethods = false)
@Profile( "test")
public class TestConfig {

    @Value("${security.username: a}")
    private String securityUserName;

    @Value("${security.password: p}")
    private String securityPassword;

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }

    @Bean
    public Stream<Note> generateNoteStream(Faker faker) {
        return Stream.generate(() -> Note.builder()
                .content(faker.lorem().paragraph())
                .build());
    }

    public RestClient restClient(int port, String controllerPath) {
        return RestClient.builder()
                .baseUrl("http://localhost:%d/%s".formatted(port, controllerPath))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic %s".formatted(
                        Base64.getEncoder().encodeToString(securityUserName.concat(":").concat(securityPassword).getBytes())))
                .build();
    }


}
