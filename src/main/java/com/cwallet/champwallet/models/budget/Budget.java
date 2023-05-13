package com.cwallet.champwallet.models.budget;

import com.cwallet.champwallet.models.account.Wallet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private double balance;
    @CreationTimestamp
    private LocalDateTime creationTime;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name="wallet_id", nullable = false)
    private Wallet wallet;

    @ToString.Exclude
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    private List<BudgetAllocationHistory> allocationHistory = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "senderBudget", cascade = CascadeType.ALL)
    private List<BudgetTransferHistory> outgoingTransferHistory = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "recipientBudget", cascade = CascadeType.ALL)
    private List<BudgetTransferHistory> incomingTransferHistory = new ArrayList<>();
}
