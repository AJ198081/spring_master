package dev.aj.spring_modulith;

import org.springframework.boot.SpringApplication;

class TestSpringModulithApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringModulithApplication::main)
				.with(PostgresTCConfig.class, TestConfig.class, TestData.class)
				.withAdditionalProfiles("test")
				.run(args);

	}
}
