package com.cwallet.CHAMPWallet.models.expense;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.models.budget.Budget;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 2048)
    private String description;
    @ManyToOne
    @JoinColumn(name = "expense_type_id", nullable = false)
    private ExpenseType expenseType;
    private Double price;
    @CreationTimestamp
    private LocalDateTime creationTime;
    @ManyToOne
    @JoinColumn(name="budget_id", nullable = false)
    private Budget budget;
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
}
