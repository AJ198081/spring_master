package dev.aj.spring_mvc;

import org.springframework.boot.SpringApplication;

public class TestHibernateJpaApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringMvcApplication::main)
//				.with(PostgresTestContainerConfiguration.class, SecurityUserInit.class, TestDataConfig.class)
				.with(PostgresTestContainerConfiguration.class, TestDataConfig.class)
				.withAdditionalProfiles("test")
				.run(args);
	}

}
