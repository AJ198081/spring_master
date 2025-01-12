package dev.aj.sdj_hibernate.domain.entities;

import com.opencsv.bean.CsvIgnore;
import dev.aj.sdj_hibernate.domain.entities.auditing.AuditMetaData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "guide", schema = "sys_org")
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "studentEntities")
@NamedEntityGraph(name = "GuideEntity.withStudents", attributeNodes = @NamedAttributeNode("studentEntities"))
public class GuideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "staff_id", unique = true)
    private String staffId;

    private BigDecimal salary;

    @OneToMany(mappedBy = "guide",
            cascade = {CascadeType.PERSIST},
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private Set<StudentEntity> studentEntities = new HashSet<>();

    @CsvIgnore
    @Embedded
    @Builder.Default //If not used, will take the value of 'auditMetaData' as null,
    // needs to be non-null, but empty so the Data Jpa can enter auditing information
    private AuditMetaData auditMetaData = new AuditMetaData();
}
