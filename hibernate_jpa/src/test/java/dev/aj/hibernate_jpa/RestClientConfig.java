package dev.aj.hibernate_jpa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@TestConfiguration
public class RestClientConfig {

    @Value("${server.port: 80}")
    private int serverPort;


}
