package dev.aj.full_stack_v3.service;

import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.ExpenseResponse;

import java.util.List;

public interface ExpenseService {

    List<ExpenseResponse> getAllExpenses();

    List<ExpenseResponse> saveExpenses(List<ExpenseRequest> randomExpenseSamples);
}
