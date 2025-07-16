package dev.aj.full_stack_v5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.aj.full_stack_v5.order.domain.mappers.CustomTimeStampSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
@Slf4j
public class FullStackV5Application {

    public static void main(String[] args) {
        SpringApplication.run(FullStackV5Application.class, args);
    }

    @Bean
    public ObjectMapper jackson2ObjectMapperBuilderCustomizer(Jackson2ObjectMapperBuilder builder) {
        builder.indentOutput(true);
//        builder.featuresToEnable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
//        builder.featuresToEnable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(java.time.ZonedDateTime.class, new CustomTimeStampSerializer());

//        builder.modules(simpleModule);

        return builder.build();
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.error("Application Started");
    }

}
