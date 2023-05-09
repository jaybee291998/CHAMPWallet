package com.cwallet.CHAMPWallet.mappers.incomeType;

import com.cwallet.CHAMPWallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.CHAMPWallet.models.income.IncomeType;

public class IncomeTypeMapper {

    public static IncomeType mapToIncomeType(IncomeTypeDto incomeTypeDTO) {
        return IncomeType.builder()
                .id(incomeTypeDTO.getId())
                .name(incomeTypeDTO.getName())
                .description(incomeTypeDTO.getDescription())
                .wallet(incomeTypeDTO.getWallet())
                .build();
    }
    public static IncomeTypeDto mapToIncomeTypeDto(IncomeType incomeType) {
        return IncomeTypeDto.builder()
                .id(incomeType.getId())
                .name(incomeType.getName())
                .description(incomeType.getDescription())
                .wallet(incomeType.getWallet())
                .build();
    }
}
