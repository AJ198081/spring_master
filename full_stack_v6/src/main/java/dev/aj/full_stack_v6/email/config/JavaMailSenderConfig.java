package dev.aj.full_stack_v6.email.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
class JavaMailSenderConfig {

    private final Environment environment;

    @Bean
    @DependsOn("setEnvironment")
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(environment.getProperty("SMTP_HOST", "production"));
        javaMailSender.setPort(environment.getProperty("SMTP_PORT", Integer.class, 25));
        javaMailSender.setProtocol(environment.getProperty("SMTP_PROTOCOL", "smtp"));
        return javaMailSender;
    }

}
