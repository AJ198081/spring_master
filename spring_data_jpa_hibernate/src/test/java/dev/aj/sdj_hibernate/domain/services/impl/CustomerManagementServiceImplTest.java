package dev.aj.sdj_hibernate.domain.services.impl;

import com.github.javafaker.Faker;
import dev.aj.sdj_hibernate.PostgresConfiguration;
import dev.aj.sdj_hibernate.domain.entities.Customer;
import dev.aj.sdj_hibernate.domain.entities.Passport;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditingConfig;
import dev.aj.sdj_hibernate.domain.repositories.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Import(value = {CustomerManagementServiceImpl.class, PostgresConfiguration.class, AuditingConfig.class, Faker.class})
@TestPropertySource(locations = {"/application-test.properties"})
@DataJpaTest
class CustomerManagementServiceImplTest {

    @Autowired
    private CustomerManagementServiceImpl customerManagementService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Faker faker;

    @Test
    void persistACustomerAndPassport() {

        Customer customer = Customer.builder()
                .name(faker.name().firstName())
                .email(faker.internet().emailAddress())
                .build();

        Passport passport = Passport.builder()
                .passportNumber(faker.numerify("P-### ### ###"))
                .countryCode(faker.country().currencyCode())
                .build();

        Customer persistedCustomer = customerManagementService.persistACustomerAndPassport(customer, passport);

        Assertions.assertNotNull(persistedCustomer);

        org.assertj.core.api.Assertions.assertThat(persistedCustomer).isNotNull()
                .extracting("passport")
                .isEqualTo(passport);

    }
}