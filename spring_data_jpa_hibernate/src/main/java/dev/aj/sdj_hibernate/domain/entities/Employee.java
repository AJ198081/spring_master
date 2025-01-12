package dev.aj.sdj_hibernate.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "employees", schema = "sys_org")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String staff_id;

    private BigDecimal salary;

    @Version
    private long version;
}
