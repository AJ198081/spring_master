package dev.aj.spring_modulith;


import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresTCConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("modulith_db")
                .withUsername("admin")
                .withPassword("password")
                .withAccessToHost(true)
                .withReuse(true)
                .withExposedPorts(5432)
                .withInitScript("db/init.sql")
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withHostConfig(new HostConfig().withPortBindings(
                            new PortBinding(Ports.Binding.bindPort(5472), new ExposedPort(5432))
                    ));
                    createContainerCmd.withName("modulith_tc_container");
                });
    }

}
