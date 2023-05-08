package com.cwallet.CHAMPWallet.dto.budget;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetDTO {
    private long id;
    private String name;
    private String description;
    private double balance;
    private boolean isEnabled;
    private LocalDateTime creationTime;
    private Wallet wallet;
}
