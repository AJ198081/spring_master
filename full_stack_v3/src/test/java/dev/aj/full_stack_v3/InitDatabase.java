package dev.aj.full_stack_v3;

import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.repository.ExpenseRepository;
import dev.aj.full_stack_v3.service.ExpenseService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

@TestComponent
@RequiredArgsConstructor
public class InitDatabase {

    public static final int MIN_ENTRIES_IN_DB = 1000;
    private final TestData testData;
    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;

    @PostConstruct
    public void init() {
        if (expenseRepository.count() <= MIN_ENTRIES_IN_DB) {

            List<ExpenseRequest> randomExpenseRequests = testData.getExpenseStream()
                    .limit(MIN_ENTRIES_IN_DB)
                    .toList();

            System.out.printf("Persisted %d expenses%n", randomExpenseRequests.size());

            expenseService.saveExpenses(randomExpenseRequests);
        }
    }
}
