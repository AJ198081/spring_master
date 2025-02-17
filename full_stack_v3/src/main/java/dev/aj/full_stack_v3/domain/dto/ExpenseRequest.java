package dev.aj.full_stack_v3.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
    private String name;
    private String note;
    private String category;

    @JsonProperty("date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate date;
    private BigDecimal amount;
}
