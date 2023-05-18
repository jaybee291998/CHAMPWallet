package com.cwallet.champwallet.dto.income;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.income.IncomeType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeJson {
    private long id;
    private String source;
    private long incomeType;
    private String description;
    private double amount;
    private LocalDateTime creationTime;
    private long wallet;
}
