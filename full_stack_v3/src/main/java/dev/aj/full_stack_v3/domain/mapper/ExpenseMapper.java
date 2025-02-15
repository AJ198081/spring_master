package dev.aj.full_stack_v3.domain.mapper;

import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.ExpenseResponse;
import dev.aj.full_stack_v3.domain.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExpenseMapper {

    Expense requestToExpense(ExpenseRequest expenseRequest);

    ExpenseRequest expenseToRequest(Expense expense);

    Expense responseToExpense(ExpenseResponse expenseResponse);

    ExpenseResponse expenseToResponse(Expense expense);

    List<ExpenseResponse> expenseListToResponseList(List<Expense> expenseList);

    List<Expense> requestsToExpenses(List<ExpenseRequest> expenseRequests);
}
