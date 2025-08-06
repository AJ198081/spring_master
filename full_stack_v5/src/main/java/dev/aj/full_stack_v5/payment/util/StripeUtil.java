package dev.aj.full_stack_v5.payment.util;

import com.stripe.Stripe;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class StripeUtil {

    private final Environment environment;


    @PostConstruct
    public void init() {
        Stripe.apiKey = environment.getProperty("stripe.private.key");
    }


    @Bean
    public Dotenv setEnvironment() {

//      Load environment variables into System properties, thus making all environment variables accessible via System.getProperty(...) or environment.getProperty(...)
        return Dotenv.configure()
                .directory("full_stack_v5")
                .filename(".env")
                .systemProperties()
                .load();
    }

}
