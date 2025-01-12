package dev.aj.sdj_hibernate;

import org.springframework.boot.SpringApplication;

public class TestSpringDataJpaHibernateApplication {

    public static void main(String[] args) {
        SpringApplication
                .from(SpringDataJpaHibernateApplication::main)
                .with(PostgresConfiguration.class, InitConfiguration.class)
                .withAdditionalProfiles( "test")
                .run(args);
    }

}
