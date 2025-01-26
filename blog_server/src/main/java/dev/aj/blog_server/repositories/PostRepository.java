package dev.aj.blog_server.repositories;

import dev.aj.blog_server.domain.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
