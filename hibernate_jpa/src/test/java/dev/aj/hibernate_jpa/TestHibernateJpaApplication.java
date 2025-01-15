package dev.aj.hibernate_jpa;

import org.springframework.boot.SpringApplication;

public class TestHibernateJpaApplication {

	public static void main(String[] args) {
		SpringApplication.from(HibernateJpaApplication::main)
//				.with(PostgresTestContainerConfiguration.class, SecurityUserInit.class, TestDataConfig.class)
				.with(SecurityUserInit.class, TestDataConfig.class)
				.withAdditionalProfiles("test")
				.run(args);
	}

}
