package com.cwallet.CHAMPWallet.service.impl.budget;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.repository.budget.BudgetRepository;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;

    @Override
    public boolean save(BudgetDTO budgetDTO) {
        return false;
    }
}
