package dev.aj.kafka.config;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConfig {

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

    @Bean
    public Dotenv setEnvironment() {

        String projectDirectory = environment.getProperty("project.basedir", "./");
//      Load environment variables into System properties, thus making all environment variables accessible via System.getProperty(...) or environment.getProperty(...)
        return Dotenv.configure()
                .directory(projectDirectory)
                .filename(".env")
                .systemProperties()
                .load();
    }

}
