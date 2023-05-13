package com.cwallet.champwallet.repository.budget;

import com.cwallet.champwallet.models.budget.BudgetTransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetTransferHistoryRepository extends JpaRepository<BudgetTransferHistory, Long> {
}
