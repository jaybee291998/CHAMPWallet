package com.cwallet.CHAMPWallet.repository.budget;

import com.cwallet.CHAMPWallet.models.budget.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
