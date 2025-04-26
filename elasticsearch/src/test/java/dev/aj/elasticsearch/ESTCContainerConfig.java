package dev.aj.elasticsearch;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ESTCContainerConfig {

    @Bean
    @ServiceConnection
    public ElasticsearchContainer elasticsearchContainer() {

        return new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.17.2")
                .withEnv("xpack.security.enabled", "false")
                .withEnv("xpack.security.http.ssl.enabled", "false")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("elasticsearch-test-container"))
                .withReuse(false);
                /*.withExposedPorts(9200, 9300)
                .withCreateContainerCmdModifier(cmd -> {
                    cmd.withName("elasticsearch-test-container");
                    cmd.withHostConfig(new HostConfig().withPortBindings(
                            new PortBinding(Ports.Binding.bindPort(9223), new ExposedPort(9200))
                    ));
                });*/
    }

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withDatabaseName("es_test_db")
                .withUsername("admin")
                .withPassword("password")
                .withAccessToHost(true)
                .withReuse(true)
                .withExposedPorts(5432)
                .withInitScript("db/init.sql")
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd.withHostConfig(new HostConfig().withPortBindings(
                            new PortBinding(Ports.Binding.bindPort(10002), new ExposedPort(5432))
                    ));

                    createContainerCmd.withName("es_postgres_tc_container");
                });
    }
}
