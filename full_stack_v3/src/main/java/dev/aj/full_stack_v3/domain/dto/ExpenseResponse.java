package dev.aj.full_stack_v3.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private String expenseId;
    private String name;
    private String category;
    private String note;
    private LocalDate date;
    private BigDecimal amount;
}
