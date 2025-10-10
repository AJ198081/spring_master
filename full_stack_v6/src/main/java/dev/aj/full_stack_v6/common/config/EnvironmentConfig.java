package dev.aj.full_stack_v6.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class EnvironmentConfig {

    private final Environment environment;

    @Bean(name = "dotEnv")
    public Dotenv setEnvironment() {

        String directoryPath = environment.getProperty("project.base.dir", "./");

//      Load environment variables into System properties, thus making all environment variables accessible via System.getProperty(...) or environment.getProperty(...)
        return Dotenv.configure()
                .directory(directoryPath)
                .filename(".env")
                .systemProperties()
                .load();
    }

}
