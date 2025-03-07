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

    private final TestData testData;
    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;

    @PostConstruct
    public void init() {
        if (expenseRepository.count() <=50) {
            List<ExpenseRequest> randomExpenseRequests = testData.getExpenseStream().limit(50)
                    .toList();
            expenseService.saveExpenses(randomExpenseRequests);
        }
    }
}
