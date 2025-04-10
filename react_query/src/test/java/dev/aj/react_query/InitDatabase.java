package dev.aj.react_query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;


@TestComponent
@RequiredArgsConstructor
public class InitDatabase {

    private final TestData testData;

    @PostConstruct
    public void init() {
//                Do something here
    }
}
