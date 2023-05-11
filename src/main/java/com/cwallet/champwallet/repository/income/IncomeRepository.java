package com.cwallet.champwallet.repository.income;
import com.cwallet.champwallet.models.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {
        List<Income> findByWalletId(Long walletID);
        List<Income> findByIncomeTypeId(Long incomeTypeId);
        Optional<Income> findById(Long incomeId);
   Income findByIdAndWalletId(long incomeID, long walletID);
}
