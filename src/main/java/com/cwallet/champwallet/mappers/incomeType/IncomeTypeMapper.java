package com.cwallet.champwallet.mappers.incomeType;

import com.cwallet.champwallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.champwallet.models.income.IncomeType;

public class IncomeTypeMapper {

    public static IncomeType mapToIncomeType(IncomeTypeDto incomeTypeDTO) {
        return IncomeType.builder()
                .id(incomeTypeDTO.getId())
                .name(incomeTypeDTO.getName())
                .description(incomeTypeDTO.getDescription())
                .wallet(incomeTypeDTO.getWallet())
                .creationTime(incomeTypeDTO.getCreationTime())
                .build();
    }
    public static IncomeTypeDto mapToIncomeTypeDto(IncomeType incomeType) {
        return IncomeTypeDto.builder()
                .id(incomeType.getId())
                .name(incomeType.getName())
                .description(incomeType.getDescription())
                .wallet(incomeType.getWallet())
                .creationTime(incomeType.getCreationTime())
                .build();
    }
}
