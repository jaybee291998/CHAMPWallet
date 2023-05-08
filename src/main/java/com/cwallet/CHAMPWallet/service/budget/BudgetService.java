package com.cwallet.CHAMPWallet.service.budget;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;

import java.util.List;

public interface BudgetService {
    boolean save(BudgetDTO budgetDTO);
    List<BudgetDTO> getAllUserBudget();
}
