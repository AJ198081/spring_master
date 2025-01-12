package dev.aj.sdj_hibernate;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.javafaker.Faker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Locale;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("spring-data-jpa-hibernate")
                .withUsername("admin")
                .withPassword("password")
                .withAccessToHost(true)
                .withReuse(true)
                .withExposedPorts(5432)
                .withInitScript("data/init.sql")
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withHostConfig(new HostConfig().withPortBindings(
                            new PortBinding(Ports.Binding.bindPort(5433), new ExposedPort(5432))
                    ));
                });
    }

    @Bean
    public Faker faker() {
        return new Faker(Locale.of("en", "IND"));
    }

}
