package dev.aj.ecommerce.auth.domain.entities;

import dev.aj.ecommerce.auth.domain.entities.audit.AuditMetaData;
import dev.aj.ecommerce.auth.domain.entities.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users", schema = "auth")
@Audited(withModifiedFlag = true)
@AuditTable(schema = "auth", value = "user_audit")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_seq",
            sequenceName = "user_seq",
            schema = "auth",
            allocationSize = 10, initialValue = 1000)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    @Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "VARCHAR(100)")
    private String password;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(100)")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    private String email;

    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(10)")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();
}

