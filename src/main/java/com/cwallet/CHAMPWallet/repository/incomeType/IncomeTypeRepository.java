package com.cwallet.CHAMPWallet.repository.incomeType;

import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeTypeRepository extends JpaRepository<IncomeType, Long> {
}
