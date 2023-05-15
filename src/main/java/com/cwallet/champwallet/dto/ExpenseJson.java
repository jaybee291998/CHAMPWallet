package com.cwallet.champwallet.dto;

import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.expense.ExpenseType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseJson {
    private long id;
    private long budgetID;
    private long expenseTypeID;
    private String description;
    private double price;
    private LocalDateTime creationTime;
    private long walletID;
}
