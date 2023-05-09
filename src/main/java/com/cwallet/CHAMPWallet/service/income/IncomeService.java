package com.cwallet.CHAMPWallet.service.income;

import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;

import java.util.List;

public interface IncomeService {
    List<IncomeDTO> getAllUserIncome();
}
