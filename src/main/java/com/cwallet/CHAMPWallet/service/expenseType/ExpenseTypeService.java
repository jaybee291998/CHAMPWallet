package com.cwallet.CHAMPWallet.service.expenseType;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import java.util.List;

public interface ExpenseTypeService {
    void save(ExpenseTypeDto expenseTypeDto);

    List<ExpenseType> findAll(ExpenseTypeDto expenseTypeDto);
}
