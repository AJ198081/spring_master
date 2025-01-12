package dev.aj.sdj_hibernate.domain.entities.projections;

import java.math.BigDecimal;

public record EmployeeProjection(String name, BigDecimal salary) {

    String getFormattedSalary() {
        return String.format("%s - %.2f", name, salary);
    }
}
