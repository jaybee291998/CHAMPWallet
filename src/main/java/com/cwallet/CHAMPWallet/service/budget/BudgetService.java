package com.cwallet.CHAMPWallet.service.budget;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.exception.AccountingConstraintViolationException;
import com.cwallet.CHAMPWallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.CHAMPWallet.exception.budget.BudgetExpiredException;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;

import java.util.List;

public interface BudgetService {
    boolean save(BudgetDTO budgetDTO);
    List<BudgetDTO> getAllUserBudget();

    BudgetDTO getSpecificBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized;
    void update(BudgetDTO budgetDTO, long budgetID) throws NoSuchBudgetOrNotAuthorized;
    boolean isUpdateable(BudgetDTO budgetDTO);
    void deleteBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException;
    void allocateToBudget(long budgetID, double amount, String description, boolean isAllocate) throws NoSuchEntityOrNotAuthorized, AccountingConstraintViolationException;
}
