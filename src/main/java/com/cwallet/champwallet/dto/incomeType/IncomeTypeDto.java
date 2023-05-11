package com.cwallet.champwallet.dto.incomeType;

import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.utils.ExpirableAndOwned;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeTypeDto implements ExpirableAndOwned {
    private long id;
    private String name;
    private String description;
    @ToString.Exclude
    private Wallet wallet;
    private LocalDateTime creationTime;

    @Override
    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public Wallet getOwner() {
        return wallet;
    }
}
