package dev.aj.react_query.repositories;

import dev.aj.react_query.domain.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
