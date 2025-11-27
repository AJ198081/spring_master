package dev.aj.full_stack_v3.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@RevisionEntity
@Table(name = "revinfo", schema = "expense")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomRevisionEntity {

    @Id
    @GeneratedValue
    @RevisionNumber
    private Integer id;

    @RevisionTimestamp
    private Long revisionTimestamp;
}
