package com.cwallet.CHAMPWallet.service.expenseType;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.models.expenseType.ExpenseType;

public interface ExpenseTypeService {
    void save(ExpenseTypeDto expenseTypeDto);
}
