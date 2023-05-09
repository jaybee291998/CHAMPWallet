package com.cwallet.CHAMPWallet.dto.income;
import com.cwallet.CHAMPWallet.models.account.Wallet;
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

public class IncomeDTO {
    private long id;
    private String source;
    private String description;
    private double balance;
    private double amount;
    private boolean isEnabled;
    private LocalDateTime creationTime;
    @ToString.Exclude
    private Wallet wallet;
}
