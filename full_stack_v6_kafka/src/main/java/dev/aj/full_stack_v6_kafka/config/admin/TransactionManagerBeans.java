package dev.aj.full_stack_v6_kafka.config.admin;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class TransactionManagerBeans {

    @Bean
    @Primary
    public KafkaTransactionManager<Object, Object> kafkaTransactionManager(ProducerFactory<Object, Object> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    @Bean("transactionManager")
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
