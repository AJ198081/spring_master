package dev.aj.api_gateway;

import org.springframework.boot.SpringApplication;

class TestApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.from(ApiGatewayApplication::main)
                .run(args);
    }

}
