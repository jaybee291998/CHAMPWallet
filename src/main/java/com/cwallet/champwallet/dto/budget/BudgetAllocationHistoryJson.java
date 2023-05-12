package com.cwallet.champwallet.dto.budget;

import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetAllocationHistoryJson {
    private Long id;
    private String description;
    private Long walletID;
    private Long budgetID;
    private Double amount;
    private Boolean isAllocate;
    private LocalDateTime creationTime;
}
