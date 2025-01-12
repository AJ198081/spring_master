package dev.aj.sdj_hibernate.domain.entities;

import dev.aj.sdj_hibernate.domain.entities.auditing.AuditMetaData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "movies", schema = "sys_org")
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "actors")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "movie_actor",
            schema = "sys_org",
            joinColumns = {@JoinColumn(name = "movie_id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id")}
    )
    @Builder.Default
    private Set<Actor> actors = new HashSet<>();

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

}
