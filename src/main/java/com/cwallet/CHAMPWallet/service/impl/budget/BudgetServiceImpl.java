package com.cwallet.CHAMPWallet.service.impl.budget;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.models.budget.Budget;
import com.cwallet.CHAMPWallet.repository.budget.BudgetRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cwallet.CHAMPWallet.mappers.budget.BudgetMapper.mapToBudget;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Override
    public boolean save(BudgetDTO budgetDTO) {
        Budget budget = mapToBudget(budgetDTO);
        budget.setWallet(securityUtil.getLoggedInUser().getWallet());
        budget.setEnabled(true);
        budget.setBalance(0);
        try {
            budgetRepository.save(budget);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
