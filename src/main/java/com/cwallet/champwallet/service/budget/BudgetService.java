package com.cwallet.champwallet.service.budget;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.BudgetExpiredException;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.models.budget.Budget;

import java.util.List;

public interface BudgetService {
    boolean save(BudgetDTO budgetDTO);
    List<BudgetDTO> getAllUserBudget();

    BudgetDTO getSpecificBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized;

    void update(BudgetDTO budgetDTO, long budgetID) throws NoSuchBudgetOrNotAuthorized;
    boolean isUpdateable(BudgetDTO budgetDTO);
    void deleteBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException, AccountingConstraintViolationException;
    void allocateToBudget(long budgetID, double amount, String description, boolean isAllocate) throws NoSuchEntityOrNotAuthorized, AccountingConstraintViolationException;
    void fundTransferToOtherBudget(long senderBudgetID, long recipientBudgetID, String description, double amount) throws NoSuchEntityOrNotAuthorized, AccountingConstraintViolationException;
    Budget getBudget(long budgetID) throws NoSuchEntityOrNotAuthorized;
}
