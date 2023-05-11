package com.cwallet.CHAMPWallet.service.income;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;
import com.cwallet.CHAMPWallet.exception.income.IncomeExpiredException;
import com.cwallet.CHAMPWallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.income.IncomeType;

import java.util.List;

public interface IncomeService {
    boolean save(IncomeDTO incomeDTO, IncomeType incomeType);
    List<IncomeDTO> getAllUserIncome();
//    void delete(Long incomeId);
IncomeDTO getSpecificIncome(long incomeID) throws NoSuchIncomeOrNotAuthorized;

boolean isUpdateable(IncomeDTO incomeDto);
    void update(IncomeDTO incomeDTO, long incomeID) throws NoSuchIncomeOrNotAuthorized;
    void deleteIncome(long budgetID) throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException;
}
