package com.cwallet.CHAMPWallet.repository.expenseType;

import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {

    @Query("SELECT * FROM ExpenseType WHERE walletId = :walletId")
    ExpenseType findById(@Param("walletId") String walletId);
}
