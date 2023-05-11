package com.cwallet.champwallet.models.budget;

import com.cwallet.champwallet.models.account.Wallet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "budget_allocation_history")
@Data
public class BudgetAllocationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(length = 2048)
    private String description;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    private Double amount;
    private Boolean isAllocate;
    @CreationTimestamp
    private LocalDateTime creationTime;
}
