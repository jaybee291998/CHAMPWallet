package com.cwallet.champwallet.models.income;

import com.cwallet.champwallet.models.account.Wallet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="income_type")
public class IncomeType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    @Column(length = 2048)
    private String description;
    @CreationTimestamp
    private LocalDateTime creationTime;
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    @ToString.Exclude
    @OneToMany(mappedBy = "incomeType", cascade = CascadeType.ALL)
    private List<Income> incomes = new ArrayList<>();
}
