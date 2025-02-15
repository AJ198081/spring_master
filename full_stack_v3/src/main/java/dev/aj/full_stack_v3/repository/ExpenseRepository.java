package dev.aj.full_stack_v3.repository;

import dev.aj.full_stack_v3.domain.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
