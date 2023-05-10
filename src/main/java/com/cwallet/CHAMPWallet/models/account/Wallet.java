package com.cwallet.CHAMPWallet.models.account;

import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="wallet")
@Builder
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private double balance = 0;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="account_id", referencedColumnName = "id")
    private UserEntity user;
    @ToString.Exclude
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.REMOVE)
    private List<ExpenseType> expenseTypes = new ArrayList<>();
    @ToString.Exclude
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.REMOVE)
    private List<IncomeType> incomeTypes = new ArrayList<>();

}
