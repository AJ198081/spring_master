package dev.aj.full_stack_v6;

import dev.aj.full_stack_v6.common.domain.config.AuditingEntityConfig;
import dev.aj.full_stack_v6.security.config.beans.AuthManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(
        value = {
                AuthManager.class,
                AuditingEntityConfig.class
        }
)
public class ApplicationModuleTests {

    @Test
    void verifyApplicationModules() {
        ApplicationModules.of(
                FullStackV6Application.class
        ).verify();
    }

    @Test
    void writeApplicationDocumentation() {

        new Documenter(ApplicationModules.of(FullStackV6Application.class))
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
