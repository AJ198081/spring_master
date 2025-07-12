package dev.aj.full_stack_v5;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
@Disabled("Demo only")
class SpringModulithTest {

    @Disabled("Demo only")
    @Test
    void verifyModule() {
        ApplicationModules.of(FullStackV5Application.class).verify();
    }
}
