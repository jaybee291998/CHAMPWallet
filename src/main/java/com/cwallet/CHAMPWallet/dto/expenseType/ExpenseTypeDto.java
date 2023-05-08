package com.cwallet.CHAMPWallet.dto.expenseType;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseTypeDto {
    private long id;
    private String name;
    private String description;
    private boolean isEnabled;
    private Wallet wallet;

}
