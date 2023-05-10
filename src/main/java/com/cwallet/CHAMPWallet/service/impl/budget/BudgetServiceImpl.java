package com.cwallet.CHAMPWallet.service.impl.budget;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.budget.Budget;
import com.cwallet.CHAMPWallet.repository.budget.BudgetRepository;
import com.cwallet.CHAMPWallet.repository.expense.ExpenseRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cwallet.CHAMPWallet.mappers.budget.BudgetMapper.mapToBudget;
import static com.cwallet.CHAMPWallet.mappers.budget.BudgetMapper.mapToBudgetDTO;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    private ExpenseRepository expenseRepository;
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

    @Override
    public List<BudgetDTO> getAllUserBudget() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<Budget> usersBudget = budgetRepository.findByWalletId(loggedInUser.getWallet().getId());
        return usersBudget.stream().map(budget -> mapToBudgetDTO(budget)).collect(Collectors.toList());
    }

    @Override
    public BudgetDTO getSpecificBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, loggedInUser.getWallet().getId());
        if(budget == null) {
            throw new NoSuchBudgetOrNotAuthorized("Not authorized or doesnt exsit");
        }
        BudgetDTO budgetDTO = mapToBudgetDTO(budget);
        return budgetDTO;
    }

    @Override
    public void update(BudgetDTO budgetDTO, long budgetID) throws NoSuchBudgetOrNotAuthorized {
        if(budgetDTO == null) {
            throw new IllegalArgumentException("budget dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, loggedInUser.getWallet().getId());
        if(budget == null) {
            throw new NoSuchBudgetOrNotAuthorized("No such budget or unauthorized");
        }
        budget.setName(budgetDTO.getName());
        budget.setDescription(budgetDTO.getDescription());
        budgetRepository.save(budget);
    }

    @Override
    public boolean isUpdateable(BudgetDTO budgetDTO){
        if(expirableAndOwnedService.isExpired(budgetDTO)) {
            return false;
        } else {
            return expenseRepository.findByBudgetId(budgetDTO.getId()).isEmpty();
        }
    }
}
