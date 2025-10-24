package dev.aj.full_stack_v6.common.kafka.config;

import dev.aj.full_stack_v6.common.domain.events.OrderPlacedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.events.EventExternalizationConfiguration;
import org.springframework.modulith.events.RoutingTarget;

import java.util.Map;
import java.util.UUID;

/**
 * Use the beans from the EventExternalizationConfiguration to configure the event serialization.
 * Based on an 'externalized' annotation, you can configure the routing of events to a specific topic, or add custom headers.
 * E.g., for each OrderPlacedEvent, I am adding a custom header 'messageId' with a random UUID. To help make Consumer idempotent.
 */
@Configuration
public class EventSerializsationConfig {

    @Bean
    public EventExternalizationConfiguration eventExternalizationConfiguration() {
        return EventExternalizationConfiguration.externalizing()
                .select(EventExternalizationConfiguration.annotatedAsExternalized())
                .route(
                        OrderPlacedEvent.class,
                        router -> RoutingTarget
                                .forTarget("orders")
                                .andKey(String.valueOf(router.customerId()))
                )
                .headers(_ -> Map.of("messageId", UUID.randomUUID().toString()))
                .build();
    }

}
