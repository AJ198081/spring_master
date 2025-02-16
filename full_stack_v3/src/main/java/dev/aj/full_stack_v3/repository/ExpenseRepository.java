package dev.aj.full_stack_v3.repository;

import dev.aj.full_stack_v3.domain.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByExpenseId(UUID expenseId);
}
