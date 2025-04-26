package dev.aj.java;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(PostgresTCConfiguration.class)
@SpringBootTest
class JavaApplicationTests {

	@Test
	void contextLoads() {
	}

}
