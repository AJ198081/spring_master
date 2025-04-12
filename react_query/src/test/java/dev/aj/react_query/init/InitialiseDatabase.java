package dev.aj.react_query.init;

import dev.aj.react_query.TestData;
import dev.aj.react_query.services.PostService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
@RequiredArgsConstructor
@Slf4j
public class InitialiseDatabase {

    private final PostService postService;
    private final TestData testData;

    private static final int WRITE_POSTS_NUMBER = 100;

    @Transactional
    @PostConstruct
    public void init() {
        try {
            if (postService.count() == 0) {
                testData.getPostStream().limit(WRITE_POSTS_NUMBER).forEach(postService::save);
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
