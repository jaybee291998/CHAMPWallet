package com.cwallet.champwallet.mappers.budget;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.dto.budget.BudgetJson;
import com.cwallet.champwallet.models.budget.Budget;

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

    public static BudgetJson mapToBudgetJson(Budget budget) {
        return BudgetJson.builder()
                .id(budget.getId())
                .name(budget.getName())
                .description(budget.getDescription())
                .balance(budget.getBalance())
                .isEnabled(budget.isEnabled())
                .creationTime(budget.getCreationTime())
                .walletID(budget.getWallet().getId())
                .build();
    }
}
