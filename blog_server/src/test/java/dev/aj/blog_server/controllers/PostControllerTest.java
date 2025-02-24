package dev.aj.blog_server.controllers;

import com.github.javafaker.Faker;
import dev.aj.blog_server.PostgresTestContainerConfiguration;
import dev.aj.blog_server.TestConfig;
import dev.aj.blog_server.domain.entities.Comment;
import dev.aj.blog_server.domain.entities.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(value = {PostgresTestContainerConfiguration.class, TestConfig.class})
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostControllerTest {

    @Autowired
    private RestClient restClient;

    @Autowired
    private TestConfig testConfig;
    @Autowired
    private Faker faker;

    @Test
    void getPosts() {
    }

    @Test
    void createPostWithoutComments() {
        Post post = testConfig.generateStreamOfPosts(faker).limit(1).findFirst().orElseThrow();

        ResponseEntity<Post> entity = restClient.post()
                .uri("/posts")
                .body(post)
                .retrieve()
                .toEntity(Post.class);

        assertEquals(200, entity.getStatusCode().value());
        assertNotNull(entity.getBody());
        assertEquals(post.getTitle(), entity.getBody().getTitle());
    }

    @Test
    void createPostWithComments() {
        Post post = testConfig.generateStreamOfPosts(faker).limit(1).findFirst().orElseThrow();
        Comment comment = testConfig.generateStreamOfComments(faker).limit(1).findFirst().orElseThrow();
        Set<Comment> comments = new HashSet<>();
        comments.add(comment);
        post.setComments(comments);

        ResponseEntity<Post> entity = restClient.post()
                .uri("/posts")
                .body(post)
                .retrieve()
                .toEntity(Post.class);
        assertEquals(200, entity.getStatusCode().value());
        assertNotNull(entity.getBody());
        assertEquals(post.getTitle(), entity.getBody().getTitle());

        ResponseEntity<Set<Comment>> commentsForThisPost = restClient.get()
                .uri("/comments/postId/{postId}", entity.getBody().getId())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(200, commentsForThisPost.getStatusCode().value());
        assertNotNull(commentsForThisPost.getBody());
        assertEquals(1, commentsForThisPost.getBody().size());
        assertEquals(comment.getText(), commentsForThisPost.getBody().iterator().next().getText());


    }
}