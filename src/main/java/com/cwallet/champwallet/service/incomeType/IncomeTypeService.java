package com.cwallet.champwallet.service.incomeType;
import com.cwallet.champwallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.champwallet.exception.EntityExpiredException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.models.income.IncomeType;

import java.util.List;

public interface IncomeTypeService {
    boolean save (IncomeTypeDto incomeTypeDto);

    List<IncomeTypeDto> getAllIncomeType();

    IncomeTypeDto getIncomeTypeById(long id) throws NoSuchEntityOrNotAuthorized;
    IncomeType getIncomeType(long id) throws NoSuchEntityOrNotAuthorized;

    void update(IncomeTypeDto incomeTypeDto, long id) throws NoSuchEntityOrNotAuthorized;
    boolean isUpdateable(IncomeTypeDto incomeTypeDto);

    void deleteIncomeType (long id) throws NoSuchEntityOrNotAuthorized, EntityExpiredException;;

}
