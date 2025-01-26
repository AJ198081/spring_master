package dev.aj.blog_server.controllers;

import dev.aj.blog_server.domain.entities.Comment;
import dev.aj.blog_server.repositories.CommentRepository;
import dev.aj.blog_server.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final CommentService commentService;

    @GetMapping(path = "/postId/{postId}")
    public ResponseEntity<Set<Comment>> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentRepository.findAllCommentsByPost_Id(postId));
    }

    @PostMapping(path = "/postId/{postId}")
    public ResponseEntity<Comment> addPost(@PathVariable Long postId, @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.addComment(comment, postId));
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.addComment(comment));
    }
}
