package com.cwallet.champwallet.models.budget;

import com.cwallet.champwallet.models.account.Wallet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name="budget_transfer_history")
public class BudgetTransferHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "sender_budget_id", nullable = false)
    private Budget senderBudget;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "recipient_budget_id", nullable = false)
    private Budget recipientBudget;

    private Double amount;
    @Column(length = 2048)
    private String description;
    @CreationTimestamp
    private LocalDateTime creationTime;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
}
