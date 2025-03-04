package dev.aj.photoappapiusers;

import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ActuatorConfig {

    @Bean
    public HttpExchangeRepository getHttpExchangeRepository() {
        return new InMemoryHttpExchangeRepository();
    }

}
