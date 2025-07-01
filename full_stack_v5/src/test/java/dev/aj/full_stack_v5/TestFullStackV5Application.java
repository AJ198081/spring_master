package dev.aj.full_stack_v5;

import org.springframework.boot.SpringApplication;

class TestFullStackV5Application {

	public static void main(String[] args) {
		SpringApplication.from(FullStackV5Application::main)
				.with(
						CORSOverride.class,
						InitSecurityUser.class,
						TestDataFactory.class,
						TestConfig.class,
						PhotosFactory.class,
						TestSecurityConfig.class
				)
				.withAdditionalProfiles("test", "local")
				.run(args);
	}

}
