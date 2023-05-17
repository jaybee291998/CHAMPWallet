package com.cwallet.champwallet.service.expense;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.expense.*;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.models.budget.Budget;



import java.util.List;

public interface ExpenseService {
    boolean save(ExpenseDTO expenseDTO, ExpenseType expenseType,Budget budget);
    List<ExpenseDTO> getAllUserExpense();
    List<ExpenseJson> getExpensesWithinInterval(int intervalInDays);

     ExpenseDTO getSpecificExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized;

    boolean isUpdateable(ExpenseDTO expenseDTO) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException;
    void update(ExpenseDTO expenseDTO, long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, AccountingConstraintViolationException, NoSuchEntityOrNotAuthorized;
 void deleteExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, NoSuchEntityOrNotAuthorized;
}
