package com.cwallet.champwallet.repository.budget;

import com.cwallet.champwallet.models.budget.BudgetAllocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BudgetAllocationHistoryRepository extends JpaRepository<BudgetAllocationHistory, Long> {
    @Query(
            value = "SELECT id, amount, creation_time, description, is_allocate, wallet_id, budget_id FROM " +
                    "budget_allocation_history WHERE budget_id=:budgetID AND creation_time BETWEEN :startDate AND :endDate AND wallet_id=:walletID",
            nativeQuery=true
    )
    List<BudgetAllocationHistory> getAllocationHistory(long budgetID, long walletID, LocalDateTime startDate, LocalDateTime endDate);
}
