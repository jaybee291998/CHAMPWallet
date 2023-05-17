package com.cwallet.champwallet.repository.income;
import com.cwallet.champwallet.models.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
        List<Income> findByWalletId(Long walletID);
        List<Income> findByWalletIdOrderByTimestamp(Long walletID);
        List<Income> findByIncomeTypeId(Long incomeTypeId);
        Optional<Income> findById(Long incomeId);
        Income findByIdAndWalletId(long incomeID, long walletID);
        @Query(
                value = "SELECT id, amount, description, source_of_income, timestamp, income_type_id, wallet_id FROM " +
                        "income WHERE timestamp BETWEEN :startDate AND :endDate AND wallet_id=:walletID ORDER BY timestamp ASC",
                nativeQuery=true
        )
        List<Income> getIncomesWithinDateRange(long walletID, LocalDateTime startDate, LocalDateTime endDate);
}
