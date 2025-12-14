package dev.aj.full_stack_v3;

import com.github.javafaker.Faker;
import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.UserRegistrationRequest;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${password.special-characters: '!@#$%^'}")
    private char[] specialCharacters;

    @Bean
    public Stream<ExpenseRequest> getExpenseStream() {

        List<String> categories = List.of("Food", "Transportation", "Entertainment", "Shopping", "Health", "Utilities", "Miscellaneous", "Housing");

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

    @Bean
    public Stream<UserRegistrationRequest> getUserRegistrationRequestStream() {

        return Stream.generate(() -> UserRegistrationRequest.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .username(faker.name().username())
                .password(getValidPassword())
                .email(faker.internet().emailAddress())
                .build());
    }

    private @Size(min = 8, message = "Password must be at least 8 characters") String getValidPassword() {

        String password = faker.internet().password(8, 50, true, true, true);

        if (!StringUtils.containsAny(password, specialCharacters)) {
            password = password.substring(0, password.length() - 1)
                    .concat(String.valueOf(specialCharacters[faker.random().nextInt(specialCharacters.length)]));
        }

        return password;
    }


}
