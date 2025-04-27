package dev.aj.ecommerce;


import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.TestPropertySource;

@ApplicationModuleTest(classes = ECommerceApplication.class)
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class ModularStructureVerificationTest {

    @Test
    public void testModularStructure() {
        ApplicationModules modules = ApplicationModules.of(ECommerceApplication.class);
        modules.verify();
    }

    @Test
    public void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(ECommerceApplication.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
