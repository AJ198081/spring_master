package dev.aj.hibernate_jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student", schema = "sc_hibernate")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "student_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(40)")
    private String firstName;
    @Column(nullable = false, columnDefinition = "VARCHAR(40)")
    private String lastName;
    @Column(nullable = false, columnDefinition = "VARCHAR(60)")
    private String email;

    @Column(columnDefinition = "VARCHAR(15)")
    private String phone;
}
