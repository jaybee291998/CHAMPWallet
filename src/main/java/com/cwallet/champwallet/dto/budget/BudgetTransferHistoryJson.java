package com.cwallet.champwallet.dto.budget;

import com.cwallet.champwallet.models.budget.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetTransferHistoryJson {
    private Long id;
    private Long senderBudgetID;
    private Long recipientBudgetID;
    private Double amount;
    private String description;
    private LocalDateTime creationTime;
    private Long walletID;
}
