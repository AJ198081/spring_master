package dev.aj.talent_request.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/talent-requests")
@RequiredArgsConstructor
public class TalentRequestController {

    @Value("${spring.application.name:unknown}")
    private String serviceName;

    private final Environment environment;

    @RequestMapping("/service-info")
    public String hello() {
        return "%s is running on port %s".formatted(serviceName, environment.getProperty("local.server.port"));
    }

}
