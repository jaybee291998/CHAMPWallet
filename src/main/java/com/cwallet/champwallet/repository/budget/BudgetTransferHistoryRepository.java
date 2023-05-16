package com.cwallet.champwallet.repository.budget;

import com.cwallet.champwallet.models.budget.BudgetTransferHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BudgetTransferHistoryRepository extends JpaRepository<BudgetTransferHistory, Long> {
    @Query(
            value = "SELECT id, amount, creation_time, description, recipient_budget_id, sender_budget_id, wallet_id FROM " +
                    "budget_transfer_history WHERE sender_budget_id=:senderBudgetID AND creation_time BETWEEN :startDate AND :endDate AND wallet_id=:walletID",
            nativeQuery=true
    )
    List<BudgetTransferHistory> budgetTransferredTo(long senderBudgetID, LocalDateTime startDate, LocalDateTime endDate, long walletID);
    @Query(
            value = "SELECT id, amount, creation_time, description, recipient_budget_id, sender_budget_id, wallet_id FROM " +
                    "budget_transfer_history WHERE recipient_budget_id=:recipientBudgetID AND creation_time BETWEEN :startDate AND :endDate AND wallet_id=:walletID",
            nativeQuery=true
    )
    List<BudgetTransferHistory> budgetReceivedFrom(long recipientBudgetID, LocalDateTime startDate, LocalDateTime endDate, long walletID);

}
