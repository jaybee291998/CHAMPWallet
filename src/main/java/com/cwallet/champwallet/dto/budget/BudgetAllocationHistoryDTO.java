package com.cwallet.champwallet.dto.budget;

import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetAllocationHistoryDTO {
    private Long id;
    private String description;
    @ToString.Exclude
    private Wallet wallet;
    @ToString.Exclude
    private Budget budget;
    private Double amount;
    private Boolean isAllocate;
    private LocalDateTime creationTime;
}
