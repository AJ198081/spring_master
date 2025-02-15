package dev.aj.full_stack_v3.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "expenses", schema = "expense")
@EntityListeners(AuditingEntityListener.class)
public class Expense {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expense_seq")
    @SequenceGenerator(name = "expense_seq", sequenceName = "expense.expense_seq", allocationSize = 10, initialValue = 6793)
    private Long id;

    @Builder.Default
    @Column(unique = true, nullable = false, updatable = false, columnDefinition = "uuid", name = "expense_id", length = 36)
    private UUID expenseId = UUID.randomUUID();

    private String name;
    private String note;
    private String category;
    private String date;
    private String amount;

    @Builder.Default
    @Embedded
    private AuditMetaData auditMetaData = new AuditMetaData();

}
