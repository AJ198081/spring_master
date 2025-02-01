package dev.aj.full_stack_v2.domain.entities;

import dev.aj.full_stack_v2.domain.entities.audit.AuditEmbeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "notes")
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "note_sequence_generator")
    @SequenceGenerator(name = "note_sequence_generator", sequenceName = "note_sequence", allocationSize = 10, initialValue = 100)
    private Long id;

    @Lob
    private String content;

    private String ownerUsername;

    @Builder.Default
    @Embedded
    private AuditEmbeddable auditEmbeddable = new AuditEmbeddable();
}
