package com.cwallet.CHAMPWallet.models.account;

import com.cwallet.CHAMPWallet.models.expenseType.ExpenseType;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.REMOVE)
    private List<ExpenseType> expenseTypes = new ArrayList<>();

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.REMOVE)
    private List<IncomeType> incomeTypes = new ArrayList<>();

}
