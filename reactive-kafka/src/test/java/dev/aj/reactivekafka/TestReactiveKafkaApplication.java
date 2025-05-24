package dev.aj.reactivekafka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

class TestReactiveKafkaApplication {

	public static void main(String[] args) {
		SpringApplication.from(ReactiveKafkaApplication::main)
				.with(KafkaConsumerBootstrap.class)
				.withAdditionalProfiles("test")
				.run(args);
	}

}
