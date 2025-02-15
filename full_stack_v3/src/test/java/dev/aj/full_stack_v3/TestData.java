package dev.aj.full_stack_v3;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
public class TestData {

    private final Faker faker;

    @Bean
    public Stream<ExpenseRequest> getExpenseStream() {

        List<String> categories = List.of("Food", "Transportation", "Entertainment", "Shopping", "Health", "Utilities");

        return Stream.generate(() -> ExpenseRequest.builder()
                .name(faker.letterify("Expense-???", true))
                .date(faker.date()
                        .between(
                                Date.from(LocalDate.now().minusDays(365).atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
                        ).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .note(faker.lorem().sentence())
                .category(categories.get(faker.random().nextInt(categories.size())))
                .amount(new BigDecimal(faker.numerify("###.##")))
                .build());
    }

}
