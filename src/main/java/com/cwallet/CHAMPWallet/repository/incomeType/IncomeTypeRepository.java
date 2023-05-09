package com.cwallet.CHAMPWallet.repository.incomeType;

import com.cwallet.CHAMPWallet.models.income.IncomeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeTypeRepository extends JpaRepository<IncomeType, Long> {
    List<IncomeType> findByWalletId(Long walletID);

  IncomeType findByIdAndWalletId(long id, long walletId);


}
