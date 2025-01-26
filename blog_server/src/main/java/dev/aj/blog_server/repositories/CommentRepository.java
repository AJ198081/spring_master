package dev.aj.blog_server.repositories;

import dev.aj.blog_server.domain.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Set<Comment> findAllCommentsByPost_Id(Long postId);
}
