package com.cwallet.CHAMPWallet.dto.budget;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.models.budget.Budget;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
