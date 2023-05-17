package com.cwallet.champwallet.service.income;

import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.models.income.IncomeType;

import java.util.List;

public interface IncomeService {


    boolean save(IncomeDTO incomeDTO, IncomeType incomeType) throws AccountingConstraintViolationException, NoSuchEntityOrNotAuthorized;
    List<IncomeDTO> getAllUserIncome();

IncomeDTO getSpecificIncome(long incomeID) throws NoSuchIncomeOrNotAuthorized;

boolean isUpdateable(IncomeDTO incomeDto);
    void update(IncomeDTO incomeDTO, long incomeID) throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException, AccountingConstraintViolationException;
    void deleteIncome(long budgetID) throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException, AccountingConstraintViolationException;


}
