package com.cwallet.CHAMPWallet.repository.expenseType;

import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {

}
