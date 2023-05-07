package com.cwallet.CHAMPWallet.repository.expenseType;

import com.cwallet.CHAMPWallet.models.expenseType.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Long> {
}
