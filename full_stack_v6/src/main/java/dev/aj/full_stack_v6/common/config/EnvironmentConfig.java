package dev.aj.full_stack_v6.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentConfig {

    @Bean
    public Dotenv setEnvironment() {

//      Load environment variables into System properties, thus making all environment variables accessible via System.getProperty(...) or environment.getProperty(...)
        return Dotenv.configure()
                .directory("./")
                .filename(".env")
                .systemProperties()
                .load();
    }

}
