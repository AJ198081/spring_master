package dev.aj.career_portal;

import org.springframework.boot.SpringApplication;

class TestCareerPortalApplication {

	public static void main(String[] args) {
		SpringApplication.from(CareerPortalApplication::main)
				.with(PostgresTestContainerConfiguration.class)
				.withAdditionalProfiles("test")
				.run(args);
	}

}
