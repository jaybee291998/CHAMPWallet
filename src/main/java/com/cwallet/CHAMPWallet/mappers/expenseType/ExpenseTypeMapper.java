package com.cwallet.CHAMPWallet.mappers.expenseType;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;

public class ExpenseTypeMapper {

    public static ExpenseType mapToExpenseType(ExpenseTypeDto expenseTypeDto){
        return ExpenseType.builder()
                .id(expenseTypeDto.getId())
                .name(expenseTypeDto.getName())
                .description(expenseTypeDto.getDescription())
                .isEnabled(expenseTypeDto.isEnabled())
                .wallet(expenseTypeDto.getWallet())
                .build();
    }

    public static ExpenseTypeDto mapToExpenseTypeDto(ExpenseType expenseType){
        return ExpenseTypeDto.builder()
                .id(expenseType.getId())
                .name(expenseType.getName())
                .description(expenseType.getDescription())
                .isEnabled(expenseType.isEnabled())
                .wallet(expenseType.getWallet())
                .build();
    }
}
