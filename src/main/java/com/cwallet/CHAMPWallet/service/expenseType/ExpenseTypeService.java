package com.cwallet.CHAMPWallet.service.expenseType;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;

import java.util.List;

public interface ExpenseTypeService {
    boolean save(ExpenseTypeDto expenseTypeDto);

    List<ExpenseTypeDto> getAllUserExpenseType();
}
