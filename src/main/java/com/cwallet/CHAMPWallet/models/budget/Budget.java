package com.cwallet.CHAMPWallet.models.budget;

import com.cwallet.CHAMPWallet.models.account.Wallet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="budget")
@Builder
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @Column(length = 2048)
    private String description;
    private boolean isEnabled;
    @CreationTimestamp
    private LocalDateTime creationTime;
    @ManyToOne
    @JoinColumn(name="wallet_id", nullable = false)
    private Wallet wallet;
}
