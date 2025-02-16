package dev.aj.full_stack_v3.controller;

import dev.aj.full_stack_v3.domain.dto.ExpenseRequest;
import dev.aj.full_stack_v3.domain.dto.ExpenseResponse;
import dev.aj.full_stack_v3.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @PostMapping
    public ResponseEntity<List<ExpenseResponse>> saveExpenses(@RequestBody List<ExpenseRequest> expensesRequest) {
        return ResponseEntity.ok(expenseService.saveExpenses(expensesRequest));
    }

    @PutMapping(path = "/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(@RequestBody ExpenseRequest expenseRequest, @PathVariable UUID expenseId) {
        return ResponseEntity.ok(expenseService.updateExpense(expenseId, expenseRequest));
    }
}

