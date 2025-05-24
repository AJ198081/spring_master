package dev.aj.reactivekafka;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ReactiveKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveKafkaApplication.class, args);
    }

    @Bean
    public Dotenv initEnvironment() {

        return Dotenv.configure()
                .directory("./")
                .filename(".env")
                .systemProperties()
                .ignoreIfMissing()
                .load();
    }

}
