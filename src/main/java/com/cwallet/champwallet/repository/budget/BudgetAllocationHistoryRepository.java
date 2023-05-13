package com.cwallet.champwallet.repository.budget;

import com.cwallet.champwallet.models.budget.BudgetAllocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetAllocationHistoryRepository extends JpaRepository<BudgetAllocationHistory, Long> {
    List<BudgetAllocationHistory> findByBudgetId(long budgetId);
}
