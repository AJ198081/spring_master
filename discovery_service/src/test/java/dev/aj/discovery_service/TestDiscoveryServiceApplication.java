package dev.aj.discovery_service;

import org.springframework.boot.SpringApplication;

class TestDiscoveryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(DiscoveryServiceApplication::main)
				.withAdditionalProfiles("test")
				.run(args);

	}

}
