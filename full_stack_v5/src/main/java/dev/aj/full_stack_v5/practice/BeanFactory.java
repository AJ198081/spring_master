package dev.aj.full_stack_v5.practice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanFactory {

    @Bean
    public CustomerLogger getCustomerLogger() {
        return new CustomerLogger();
    }


    @Slf4j
    public static class CustomerLogger {
        CustomerLogger() {
            log.info("CustomerLogger has been created");
        }
        public void log() {
            log.info("CustomerLogger has logged a message");
        }
    }

}
