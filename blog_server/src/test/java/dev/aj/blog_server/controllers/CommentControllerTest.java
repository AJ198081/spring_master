package dev.aj.blog_server.controllers;

import dev.aj.blog_server.PostgresTestContainerConfiguration;
import dev.aj.blog_server.TestConfig;
import dev.aj.blog_server.domain.entities.Comment;
import dev.aj.blog_server.domain.entities.Post;
import dev.aj.blog_server.repositories.PostRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(value = {PostgresTestContainerConfiguration.class, TestConfig.class})
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class CommentControllerTest {

    @Autowired
    private Stream<Post> postStream;

    @Autowired
    private Stream<Comment> commentStream;

    @Autowired
    private RestClient restClient;

    @Autowired
    private PostRepository postRepository;

    @Test
    void getPost() {
        List<Post> allPosts = postRepository.findAll();
        ResponseEntity<Set<Comment>> fetchedComments = restClient.get()
                .uri("/comments/postId/{postId}", allPosts.getLast().getId())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Set<Comment>>() {
                });

        assertEquals(200, fetchedComments.getStatusCode().value());
        assertNotNull(fetchedComments.getBody());
        assertThat(fetchedComments.getBody()).isNotNull();
        assertThat(fetchedComments.getBody().size()).isEqualTo(1);
    }

    @Test
    @Order(1)
    void addPost() {
        Post post = postStream.limit(1).findFirst().orElseThrow();
        Post persistedPost = postRepository.save(post);
        Comment comment = commentStream.limit(1).findFirst().orElseThrow();
        comment.setPost(post);
        ResponseEntity<Comment> entity = restClient.post()
                .uri("/comments/postId/{postId}", persistedPost.getId())
                .body(comment)
                .retrieve()
                .toEntity(Comment.class);

        assertEquals(200, entity.getStatusCode().value());
        assertNotNull(entity.getBody());
        assertThat(entity.getBody()).isNotNull();
        assertThat(entity.getBody().getId()).isNotNull();
        assertThat(entity.getBody().getText()).isEqualTo(comment.getText());
        assertThat(entity.getBody().getPost()).isNull();
    }
}