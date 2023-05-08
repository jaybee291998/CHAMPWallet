package com.cwallet.CHAMPWallet.dto.incomeType;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeTypeDto {
    private long id;
    private String name;
    private String description;
    private Wallet wallet;
}
