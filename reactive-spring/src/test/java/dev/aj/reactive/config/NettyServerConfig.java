package dev.aj.reactive.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@TestConfiguration
public class NettyServerConfig {

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        NettyReactiveWebServerFactory nettyReactiveWebServerFactory = new NettyReactiveWebServerFactory(5455);
        nettyReactiveWebServerFactory.addServerCustomizers(server ->
                server.requestTimeout(Duration.ofMinutes(2L)));
        return nettyReactiveWebServerFactory;
    }

}
