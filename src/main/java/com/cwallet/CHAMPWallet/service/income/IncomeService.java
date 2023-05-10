package com.cwallet.CHAMPWallet.service.income;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.exception.income.NoSuchIncomeOrNotAuthorized;

import java.util.List;

public interface IncomeService {
    boolean save(IncomeDTO incomeDTO,String incomeTypeIDStr);
    List<IncomeDTO> getAllUserIncome();
//    void delete(Long incomeId);
IncomeDTO getSpecificIncome(long incomeID) throws NoSuchIncomeOrNotAuthorized;
}
