package com.cwallet.CHAMPWallet.dto.budget;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwned;
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
public class BudgetDTO implements ExpirableAndOwned {
    private long id;
    private String name;
    private String description;
    private double balance;
    private boolean isEnabled;
    private LocalDateTime creationTime;
    @ToString.Exclude
    private Wallet wallet;

    @Override
    public Wallet getOwner() {
        return wallet;
    }
}
