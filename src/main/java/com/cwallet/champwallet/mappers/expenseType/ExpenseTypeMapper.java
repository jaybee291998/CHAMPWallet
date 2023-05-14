package com.cwallet.champwallet.mappers.expenseType;

import com.cwallet.champwallet.dto.ExpenseTypeJson;
import com.cwallet.champwallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.champwallet.models.expense.ExpenseType;

public class ExpenseTypeMapper {

    public static ExpenseType mapToExpenseType(ExpenseTypeDto expenseTypeDto){
        return ExpenseType.builder()
                .id(expenseTypeDto.getId())
                .name(expenseTypeDto.getName())
                .description(expenseTypeDto.getDescription())
                .isEnabled(expenseTypeDto.isEnabled())
                .creationTime(expenseTypeDto.getCreationTime())
                .wallet(expenseTypeDto.getWallet())
                .build();
    }

    public static ExpenseTypeDto mapToExpenseTypeDto(ExpenseType expenseType){
        return ExpenseTypeDto.builder()
                .id(expenseType.getId())
                .name(expenseType.getName())
                .description(expenseType.getDescription())
                .isEnabled(expenseType.isEnabled())
                .creationTime(expenseType.getCreationTime())
                .wallet(expenseType.getWallet())
                .build();
    }

    public static ExpenseTypeJson mapToExpenseTypeJson(ExpenseType expenseType) {
        return ExpenseTypeJson.builder()
                .id(expenseType.getId())
                .name(expenseType.getName())
                .description(expenseType.getDescription())
                .isEnabled(expenseType.isEnabled())
                .creationTime(expenseType.getCreationTime())
                .walletID(expenseType.getWallet().getId())
                .build();
    }
}
