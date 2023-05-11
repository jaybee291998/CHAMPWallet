package com.cwallet.CHAMPWallet.repository.budget;

import com.cwallet.CHAMPWallet.models.budget.BudgetAllocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetAllocationHistoryRepository extends JpaRepository<BudgetAllocationHistory, Long> {
}
