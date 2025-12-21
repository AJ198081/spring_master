package dev.aj.full_stack_v3;


import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class PostgresTCConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {

        // Configures database name, credentials, host access, and port
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("full_stack_v3")
                .withUsername("admin")
                .withPassword("password")
                .withAccessToHost(true)
                .withReuse(true)
                .withExposedPorts(5432)
                .withInitScript("db/init.sql")
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withHostConfig(new HostConfig().withPortBindings(
                            new PortBinding(Ports.Binding.bindPort(5455), new ExposedPort(5432))
                    ));

                    createContainerCmd.withName("full_stack_v3_tc_container");
                });
    }

}
