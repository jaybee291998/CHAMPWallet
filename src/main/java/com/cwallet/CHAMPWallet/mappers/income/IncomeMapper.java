package com.cwallet.CHAMPWallet.mappers.income;

import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;

import com.cwallet.CHAMPWallet.models.income.Income;

public class IncomeMapper {
    public static Income mapToIncome(IncomeDTO incomeDTO) {
        return Income.builder()
                .id(incomeDTO.getId())
                .description(incomeDTO.getDescription())
                .sourceOfIncome(incomeDTO.getSource())
                .amount(incomeDTO.getAmount())
                .timestamp(incomeDTO.getCreationTime())
                .wallet(incomeDTO.getWallet())
                .incomeType(incomeDTO.getIncomeType())
              //  .wallet(incomeDTO.)

                .build();
    }

    public static IncomeDTO mapToIncomeDTO(Income income) {
        return IncomeDTO.builder()
                .id(income.getId())
                .source(income.getSourceOfIncome())
                .description(income.getDescription())
                .amount(income.getAmount())
                .creationTime(income.getTimestamp())
                .wallet(income.getWallet())
                .incomeType(income.getIncomeType())
                .build();
    }
}
