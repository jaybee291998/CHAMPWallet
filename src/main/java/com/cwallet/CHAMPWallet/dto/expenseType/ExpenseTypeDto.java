package com.cwallet.CHAMPWallet.dto.expenseType;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwned;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Builder
public class ExpenseTypeDto implements ExpirableAndOwned {
    private long id;
    private String name;
    private String description;
    private boolean isEnabled;
    private LocalDateTime creationTime;
    @ToString.Exclude
    private Wallet wallet;

    @Override
    public Wallet getOwner() {
        return wallet;
    }
}
