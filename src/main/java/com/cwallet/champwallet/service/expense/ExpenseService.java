package com.cwallet.champwallet.service.expense;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.BudgetDisabledException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.exception.expense.*;
import com.cwallet.champwallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.models.budget.Budget;


import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {
    boolean save(ExpenseDTO expenseDTO, ExpenseType expenseType,Budget budget) throws NoSuchEntityOrNotAuthorized, NoSuchBudgetOrNotAuthorized, NoSuchExpenseTypeOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException;
    List<ExpenseDTO> getAllUserExpense(LocalDate specificDate);
    List<ExpenseJson> getExpensesWithinInterval(int intervalInDays);

     ExpenseDTO getSpecificExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized;

    boolean isUpdateable(ExpenseDTO expenseDTO) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException;
    void update(ExpenseDTO expenseDTO, long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, AccountingConstraintViolationException, NoSuchEntityOrNotAuthorized, BudgetDisabledException, NoSuchExpenseTypeOrNotAuthorized;
 void deleteExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, NoSuchEntityOrNotAuthorized, BudgetDisabledException;
}
