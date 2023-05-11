package com.cwallet.champwallet.repository.expenseType;

import com.cwallet.champwallet.models.expense.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {

    List<ExpenseType> findByWalletId(Long walletId);

    ExpenseType findByIdAndWalletId(long id, long walletId);
}
