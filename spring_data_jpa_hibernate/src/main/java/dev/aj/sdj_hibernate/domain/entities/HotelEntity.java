package dev.aj.sdj_hibernate.domain.entities;

import dev.aj.sdj_hibernate.domain.entities.auditing.AuditMetaData;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "hotel", schema = "sys_org")
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "studentEntities")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HotelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "hotel", fetch = jakarta.persistence.FetchType.LAZY, cascade = {jakarta.persistence.CascadeType.PERSIST})
    @Builder.Default
    private Set<StudentEntity> studentEntities = new HashSet<>();

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();
}
