package com.cwallet.CHAMPWallet.dto.income;
import com.cwallet.CHAMPWallet.models.account.Wallet;
import lombok.*;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
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
    private IncomeType incomeType;
    private String description;
    private double amount;
    private LocalDateTime creationTime;
    @ToString.Exclude
    private Wallet wallet;



}
