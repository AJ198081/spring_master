package dev.aj.hibernate_jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "employee", schema = "sc_hibernate")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Employee {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(60)")
    private String firstName;

    @Column(nullable = false, columnDefinition = "VARCHAR(60)")
    private String lastName;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    private String email;

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();
}
