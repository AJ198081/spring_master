package dev.aj.spring_modulith;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)
@Import(value = { PostgresTCConfig.class, TestConfig.class, TestData.class })
public class SpringModulithApplicationTest {

    @Test
    void verifyModuleStructure() {
        ApplicationModules.of(SpringModulithApplication.class).verify();
    }

}
