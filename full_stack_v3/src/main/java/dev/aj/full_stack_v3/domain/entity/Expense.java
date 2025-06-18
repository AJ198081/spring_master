package dev.aj.full_stack_v3.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "expenses", schema = "expense")
@EntityListeners(AuditingEntityListener.class)
@Audited
@AuditTable(value = "expenses_aud", schema = "expense") // Can set Schema in the properties file as well
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
    private LocalDate date;
    private BigDecimal amount;

    @Builder.Default
    @Embedded
    private AuditMetaData auditMetaData = new AuditMetaData();

}
