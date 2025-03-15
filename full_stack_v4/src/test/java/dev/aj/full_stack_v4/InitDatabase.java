package dev.aj.full_stack_v4;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class InitDatabase {


    @PostConstruct
    public void init() {
    }
}
