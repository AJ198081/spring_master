package dev.aj.blog_server.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts", schema = "bm_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_id_seq_generator")
    @SequenceGenerator(name = "post_id_seq_generator", sequenceName = "post_id_seq", schema = "bm_schema", allocationSize = 10, initialValue = 1000)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            mappedBy = "post")
    @Builder.Default
    @JsonManagedReference
    private Set<Comment> comments = new HashSet<>();

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
        comments.forEach(comment -> comment.setPost(this));
    }

}
