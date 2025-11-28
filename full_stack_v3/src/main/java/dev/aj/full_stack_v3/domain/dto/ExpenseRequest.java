package dev.aj.full_stack_v3.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.aj.full_stack_v3.domain.dto.validations.NoMoreThanAYearInFuture;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseRequest {

    @NotBlank
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Nullable
    private String note;

    @NotBlank
    private String category;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NoMoreThanAYearInFuture(message = "Date can only be at most a year in the future")
    private LocalDate date;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}
