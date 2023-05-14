package com.cwallet.champwallet.dto;

import com.cwallet.champwallet.models.account.Wallet;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseTypeJson {
    private long id;
    private String name;
    private String description;
    private boolean isEnabled;
    private LocalDateTime creationTime;
    private long walletID;
}
