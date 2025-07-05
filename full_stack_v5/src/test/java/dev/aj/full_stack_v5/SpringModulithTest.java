package dev.aj.full_stack_v5;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
class SpringModulithTest {

    @Test
    void verifyModule() {
        ApplicationModules.of(FullStackV5Application.class).verify();
    }
}
