package dev.aj.react_query.init;

import dev.aj.react_query.domain.entities.Post;
import dev.aj.react_query.services.PostService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

@TestComponent
@RequiredArgsConstructor
@Slf4j
public class InitialiseDatabase {

    private final PostService postService;
    private final TestData testData;

    private static final int WRITE_POSTS_NUMBER = 100;

    @PostConstruct
    public void init() {
        try {
            if (postService.count() <= 101) {

                List<Post> postsToWrite = testData.getPostStream()
                        .limit(WRITE_POSTS_NUMBER)
                        .toList();

                postService.saveAll(postsToWrite);

                log.info("""
                            Database initialization succeeded.
                            Wrote {} posts to the database.
                            Database now contains {} posts.
                         """,
                        WRITE_POSTS_NUMBER,
                        postService.count()
                );
            } else {
                log.info("Database is already initialized, contains {} posts. Skipping initialization.", postService.count());
            }
        } catch (Exception e) {
            log.error("Database initialization failed: {}", e.getMessage());
        }
    }


}
