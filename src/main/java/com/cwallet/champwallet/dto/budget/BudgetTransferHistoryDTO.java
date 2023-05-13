package com.cwallet.champwallet.dto.budget;

import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetTransferHistoryDTO {
    private Long id;
    private Budget senderBudget;
    private Budget recipientBudget;
    private Double amount;
    private String description;
    private LocalDateTime creationTime;
    private Wallet wallet;
}
