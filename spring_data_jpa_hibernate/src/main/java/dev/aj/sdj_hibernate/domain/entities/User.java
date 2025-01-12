package dev.aj.sdj_hibernate.domain.entities;

import dev.aj.sdj_hibernate.domain.entities.auditing.AuditMetaData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "sys_org")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Builder.Default
    @Column(name = "registration_date")
    private LocalDate registrationDate = LocalDate.now();

    private String email;

    @Builder.Default
    private int level = 0;

    @Builder.Default
    private Boolean isActive = false;

    @Version
    private Long version;

    @Embedded
    @Builder.Default //If not used, will take the value of 'auditMetaData' as null
    private AuditMetaData auditMetaData = new AuditMetaData();

    @PreRemove
    private void preRemove() {
        this.setIsActive(false);
        System.out.printf("Deleting user %s", this);
    }
}
