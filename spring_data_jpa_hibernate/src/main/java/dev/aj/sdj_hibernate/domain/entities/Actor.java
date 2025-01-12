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
@Table(name = "actors", schema = "sys_org")
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = "movies")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "actors", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @Builder.Default
    private Set<Movie> movies = new HashSet<>();

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    public void addMovie(Movie movie) {
        this.movies.add(movie);
        movie.getActors().add(this);
    }

    public void removeMovie(Movie movie) {
        this.movies.remove(movie);
        movie.getActors().remove(this);
    }

}
