package dev.aj.full_stack_v6.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class EnvironmentConfig {

    private final Environment environment;

    @Bean(name = "dotEnv")
    public Dotenv setEnvironment() {

        String[] activeProfiles = environment.getActiveProfiles();

        boolean isTestProfileActive = Arrays.asList(activeProfiles).contains("test");

        String directoryPath = isTestProfileActive ? "./" : "./full_stack_v6/";

//      Load environment variables into System properties, thus making all environment variables accessible via System.getProperty(...) or environment.getProperty(...)
        return Dotenv.configure()
                .directory(directoryPath)
                .filename(".env")
                .systemProperties()
                .load();
    }

}
