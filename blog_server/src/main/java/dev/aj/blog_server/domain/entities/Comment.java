package dev.aj.blog_server.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;

@Entity
@Table(name = "comments", schema = "bm_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_id_seq_generator")
    @SequenceGenerator(name = "comment_id_seq_generator", sequenceName = "comment_id_seq", schema = "bm_schema", allocationSize = 10, initialValue = 1000)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String text;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;

    public void setPost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }
}
