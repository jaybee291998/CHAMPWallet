package com.cwallet.CHAMPWallet.mappers.budget;

import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.models.budget.Budget;

public class BudgetMapper {
    public static Budget mapToBudget(BudgetDTO budgetDTO) {
        return Budget.builder()
                .id(budgetDTO.getId())
                .name(budgetDTO.getName())
                .description(budgetDTO.getDescription())
                .balance(budgetDTO.getBalance())
                .isEnabled(budgetDTO.isEnabled())
                .creationTime(budgetDTO.getCreationTime())
                .wallet(budgetDTO.getWallet())
                .build();
    }

    public static BudgetDTO mapToBudgetDTO(Budget budget) {
        return BudgetDTO.builder()
                .id(budget.getId())
                .name(budget.getName())
                .description(budget.getDescription())
                .balance(budget.getBalance())
                .isEnabled(budget.isEnabled())
                .creationTime(budget.getCreationTime())
                .wallet(budget.getWallet())
                .build();
    }
}
