package com.cwallet.champwallet.mappers.income;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.dto.income.IncomeDTO;

import com.cwallet.champwallet.dto.income.IncomeJson;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.models.income.Income;

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

    public static IncomeJson mapToIncomeJson(Income income) {
        return IncomeJson.builder()
                .id(income.getId())
                .source(income.getSourceOfIncome())
                .incomeType(income.getIncomeType().getId())
                .amount(income.getAmount())
                .creationTime(income.getTimestamp())
                .description(income.getDescription())
                .wallet(income.getWallet().getId())
                .build();
    }
}
