package com.cwallet.CHAMPWallet.mappers.incomeType;

import com.cwallet.CHAMPWallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.CHAMPWallet.models.income.IncomeType;

public class IncomeTypeMapper {

    public static IncomeType mapToIncomeType(IncomeTypeDto incomeTypeDTO) {
        return IncomeType.builder()
                .name(incomeTypeDTO.getName())
                .description(incomeTypeDTO.getDescription())
                .wallet(incomeTypeDTO.getWallet())
                .build();
    }
}
