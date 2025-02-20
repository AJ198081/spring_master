package dev.aj.full_stack_v3.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.ExpenseResponse;
import dev.aj.full_stack_v3.domain.entity.Expense;
import dev.aj.full_stack_v3.domain.mapper.ExpenseMapper;
import dev.aj.full_stack_v3.repository.ExpenseRepository;
import dev.aj.full_stack_v3.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseMapper expenseMapper;
    private final ExpenseRepository expenseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        expenses.sort(Comparator.comparing(Expense::getDate).reversed());

        return expenseMapper.expenseListToResponseList(expenses);
    }

    @Override
    public List<ExpenseResponse> saveExpenses(List<ExpenseRequest> expenseRequests) {

        try {
            log.debug("Saving expenses: {}", objectMapper.writeValueAsString(expenseRequests));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Expense> expenses = expenseRepository.saveAll(expenseMapper.requestsToExpenses(expenseRequests));

        return expenseMapper.expenseListToResponseList(expenses);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(UUID expenseId, ExpenseRequest expenseRequest) {

        Expense existingExpense = expenseRepository.findByExpenseId(expenseId).stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("ExpenseID %s not found".formatted(expenseId)));

        existingExpense.setName(expenseRequest.getName());
        existingExpense.setNote(expenseRequest.getNote());
        existingExpense.setAmount(expenseRequest.getAmount());
        existingExpense.setDate(expenseRequest.getDate());
        existingExpense.setCategory(expenseRequest.getCategory());

        return expenseMapper.expenseToResponse(
                expenseRepository.save(existingExpense)
        );
    }

    @Override
    public void deleteExpense(UUID expenseId) {

        expenseRepository.findByExpenseId(expenseId).stream()
                .findFirst()
                .ifPresent(expenseRepository::delete);

    }

    @Override
    public ExpenseResponse saveExpense(ExpenseRequest expenseRequest) {
        Expense expense = expenseMapper.requestToExpense(expenseRequest);
        return expenseMapper.expenseToResponse(expenseRepository.save(expense));
    }

}
