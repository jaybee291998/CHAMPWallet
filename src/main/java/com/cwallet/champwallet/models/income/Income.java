package com.cwallet.champwallet.models.income;

import com.cwallet.champwallet.models.account.Wallet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="income")
@Builder
public class Income {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 2048)
    private String description;
    private Double amount;
    @CreationTimestamp
    private LocalDateTime timestamp;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "income_type_id", nullable = false)
    private IncomeType incomeType;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    private String sourceOfIncome;
}
