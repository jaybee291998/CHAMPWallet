package com.cwallet.champwallet.repository.budget;

import com.cwallet.champwallet.models.budget.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByWalletId(Long walletID);
    Budget findByIdAndWalletId(long budgetID, long walletID);
}
