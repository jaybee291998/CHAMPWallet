package com.cwallet.champwallet.service.expense;

import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.expense.*;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.models.budget.Budget;



import java.util.List;

public interface ExpenseService {
    boolean save(ExpenseDTO expenseDTO, ExpenseType expenseType,Budget budget);
    List<ExpenseDTO> getAllUserExpense();

//     ExpenseDTO getSpecificExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized;
//
//    boolean isUpdateable(ExpenseDTO expenseDTO);
//    void update(ExpenseDTO expenseDTO, long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, AccountingConstraintViolationException;
//    void deleteExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException;
}
