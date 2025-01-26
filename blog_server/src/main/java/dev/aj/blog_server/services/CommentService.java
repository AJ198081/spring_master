package dev.aj.blog_server.services;

import dev.aj.blog_server.domain.entities.Comment;
import dev.aj.blog_server.domain.entities.Post;
import dev.aj.blog_server.repositories.CommentRepository;
import dev.aj.blog_server.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment addComment(Comment comment, Long postId) {

        comment.setPost(postRepository.findById(postId).orElseGet(() -> Post.builder()
                .title(String.format("Placeholder for missing post title - %s / %d", postId, new Random().nextInt(1000)))
                .build()));

        return commentRepository.save(comment);
    }


    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
