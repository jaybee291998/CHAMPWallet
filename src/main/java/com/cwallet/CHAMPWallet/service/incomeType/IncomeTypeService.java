package com.cwallet.CHAMPWallet.service.incomeType;
import com.cwallet.CHAMPWallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.income.IncomeType;

import java.util.List;

public interface IncomeTypeService {
    void save (IncomeTypeDto incomeTypeDto);

    List<IncomeTypeDto> getAllIncomeType();

    IncomeTypeDto getIncomeTypeById(long id) throws NoSuchBudgetOrNotAuthorized;
    IncomeType getIncomeType(long id) throws NoSuchBudgetOrNotAuthorized;

    void update(IncomeTypeDto incomeTypeDto, long id) throws NoSuchBudgetOrNotAuthorized;
    boolean isUpdateable(IncomeTypeDto incomeTypeDto);

}
