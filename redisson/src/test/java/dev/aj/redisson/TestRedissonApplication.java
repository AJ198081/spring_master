package dev.aj.redisson;

import org.springframework.boot.SpringApplication;

class TestRedissonApplication {

	public static void main(String[] args) {
		SpringApplication.from(RedissonApplication::main)
				.with(TestConfig.class, TestData.class, InitDatabase.class, CORSOverride.class)
				.withAdditionalProfiles("test")
				.run(args);
	}
}
