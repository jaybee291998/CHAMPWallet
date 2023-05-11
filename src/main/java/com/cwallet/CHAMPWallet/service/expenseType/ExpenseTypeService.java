package com.cwallet.CHAMPWallet.service.expenseType;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.exception.expenseType.ExpenseTypeExpiredException;
import com.cwallet.CHAMPWallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;

import java.util.List;

public interface ExpenseTypeService {
    boolean save(ExpenseTypeDto expenseTypeDto);

    List<ExpenseTypeDto> getAllUserExpenseType();

    ExpenseTypeDto getExpenseTypeId(long id) throws NoSuchExpenseTypeOrNotAuthorized;

    //
    void updateExpenseType(ExpenseTypeDto expenseTypeDto, long expenseTypeId) throws NoSuchExpenseTypeOrNotAuthorized;

    boolean isUpdatable(ExpenseTypeDto expenseTypeDto);

    void deleteExpenseType(long expenseTypeId) throws NoSuchExpenseTypeOrNotAuthorized, ExpenseTypeExpiredException;
}
