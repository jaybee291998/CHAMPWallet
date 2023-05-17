package com.cwallet.champwallet.service.expenseType;

import com.cwallet.champwallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.champwallet.exception.EntityExpiredException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.expenseType.ExpenseTypeExpiredException;
import com.cwallet.champwallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;

import java.util.List;

public interface ExpenseTypeService {
    boolean save(ExpenseTypeDto expenseTypeDto);

    List<ExpenseTypeDto> getAllUserExpenseType();

    ExpenseTypeDto getExpenseTypeId(long id) throws NoSuchEntityOrNotAuthorized;

    //
    void updateExpenseType(ExpenseTypeDto expenseTypeDto, long expenseTypeId) throws NoSuchEntityOrNotAuthorized;

    boolean isUpdatable(ExpenseTypeDto expenseTypeDto);

    void deleteExpenseType(long expenseTypeId) throws NoSuchEntityOrNotAuthorized, EntityExpiredException;
}
