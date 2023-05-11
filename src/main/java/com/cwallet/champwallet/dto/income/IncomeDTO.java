package com.cwallet.champwallet.dto.income;
import com.cwallet.champwallet.models.account.Wallet;
import lombok.*;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.utils.ExpirableAndOwned;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class IncomeDTO implements ExpirableAndOwned {
    private long id;
    private String source;
    private IncomeType incomeType;
    private String description;
    private double amount;
    private LocalDateTime creationTime;
    @ToString.Exclude
    private Wallet wallet;


    @Override
    public Wallet getOwner() {
        return wallet;
    }
}
