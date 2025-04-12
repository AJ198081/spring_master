package dev.aj.react_query;

import com.github.javafaker.Faker;
import dev.aj.react_query.domain.entities.Post;
import dev.aj.react_query.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final Faker faker;

    public Stream<Post> getPostStream() {

        List<User> fiveUsers = getUserStream().limit(5).toList();

        return Stream.generate(() -> Post.builder()
                .title(faker.lorem().sentence())
                .body(faker.lorem().paragraph(4))
                .user(fiveUsers.get(faker.random().nextInt(fiveUsers.size())))
                .build());
    }

    public Stream<User> getUserStream() {
        return Stream.generate(() -> User.builder()
                .username(faker.name().username())
                .build());
    }


}
