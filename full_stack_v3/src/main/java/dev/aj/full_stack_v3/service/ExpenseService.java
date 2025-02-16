package dev.aj.full_stack_v3.service;

import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.ExpenseResponse;

import java.util.List;
import java.util.UUID;

public interface ExpenseService {

    List<ExpenseResponse> getAllExpenses();

    List<ExpenseResponse> saveExpenses(List<ExpenseRequest> randomExpenseSamples);

    ExpenseResponse updateExpense(UUID expenseId, ExpenseRequest expenseRequest);
}
