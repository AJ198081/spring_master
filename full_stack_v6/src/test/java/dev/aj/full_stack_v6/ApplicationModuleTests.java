package dev.aj.full_stack_v6;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
public class ApplicationModuleTests {

    @Test
    void writeDocumentationSnippets() {
        ApplicationModules applicationModules = ApplicationModules.of(FullStackV6Application.class).verify();
    }

    @Test
    void writeApplicationDocumentation() {

        new Documenter(ApplicationModules.of(FullStackV6Application.class))
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
