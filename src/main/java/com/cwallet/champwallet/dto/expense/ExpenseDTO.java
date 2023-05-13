package com.cwallet.champwallet.dto.expense;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import lombok.*;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.utils.ExpirableAndOwned;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDTO implements ExpirableAndOwned {
    private long id;
    private Budget budget;
    private ExpenseType expenseType;
    private String description;
    private double price;
    private LocalDateTime creationTime;
    @ToString.Exclude
    private Wallet wallet;


    @Override
    public Wallet getOwner() {
        return wallet;
    }
}
