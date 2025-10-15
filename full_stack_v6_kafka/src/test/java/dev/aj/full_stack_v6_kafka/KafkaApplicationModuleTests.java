package dev.aj.full_stack_v6_kafka;

import dev.aj.full_stack_v6_kafka.config.admin.AdminConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES)
@Import({
        AdminConfiguration.class,
})
public class KafkaApplicationModuleTests {

    @Test
    void verifyApplicationModules() {
        ApplicationModules.of(FullStackV6KafkaApplication.class).verify();
    }

    @Test
    void writeApplicationDocumentation() {
        new Documenter(ApplicationModules.of(FullStackV6KafkaApplication.class))
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
