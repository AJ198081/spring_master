package dev.aj.sdj_hibernate.domain.entities.projections;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

//TODO (left on 8/01/2025): Don't use it, instead just use 'records'
public interface EmployeeNameSalary {

    String getName();
    BigDecimal getSalary();

    @Value("#{target.name} \t #{target.salary}")
    String getFormattedSalary();

}
