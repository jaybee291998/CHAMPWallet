package com.cwallet.champwallet.models.expense;

import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "expense")
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 2048)
    private String description;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "expense_type_id", nullable = false)
    private ExpenseType expenseType;
    private Double price;
    @CreationTimestamp
    private LocalDateTime creationTime;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name="budget_id", nullable = false)
    private Budget budget;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
}
