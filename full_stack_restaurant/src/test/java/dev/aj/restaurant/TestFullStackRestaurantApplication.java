package dev.aj.restaurant;

import org.springframework.boot.SpringApplication;

public class TestFullStackRestaurantApplication {

	public static void main(String[] args) {
		SpringApplication.from(FullStackRestaurantApplication::main)
				.with(TestData.class, TestConfig.class)
				.withAdditionalProfiles("test")
				.run(args);
	}

}
