package com.cwallet.champwallet.service.incomeType;
import com.cwallet.champwallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.champwallet.exception.budget.BudgetExpiredException;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.models.income.IncomeType;

import java.util.List;

public interface IncomeTypeService {
    void save (IncomeTypeDto incomeTypeDto);

    List<IncomeTypeDto> getAllIncomeType();

    IncomeTypeDto getIncomeTypeById(long id) throws NoSuchBudgetOrNotAuthorized;
    IncomeType getIncomeType(long id) throws NoSuchBudgetOrNotAuthorized;

    void update(IncomeTypeDto incomeTypeDto, long id) throws NoSuchBudgetOrNotAuthorized;
    boolean isUpdateable(IncomeTypeDto incomeTypeDto);

    void deleteIncomeType (long id) throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException;;

}
