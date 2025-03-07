package dev.aj.hibernate_jpa.entities;

/**
 * Projection for {@link Employee}
 */

public interface EmployeeInfo {
    String getFirstName();

    String getLastName();

    String getEmail();
}