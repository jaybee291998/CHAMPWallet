package com.cwallet.CHAMPWallet.service.income;

import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;

import java.util.List;

public interface IncomeService {
    boolean save(IncomeDTO incomeDTO,String incomeTypeIDStr);
    List<IncomeDTO> getAllUserIncome();
//    void delete(Long incomeId);
}
