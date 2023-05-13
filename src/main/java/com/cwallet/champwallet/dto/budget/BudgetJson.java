package com.cwallet.champwallet.dto.budget;

import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.utils.ExpirableAndOwned;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetJson {
    private long id;
    private String name;
    private String description;
    private double balance;
    private boolean isEnabled;
    private LocalDateTime creationTime;
    private Long walletID;
}
