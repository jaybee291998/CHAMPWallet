package com.cwallet.champwallet.mappers.budget;

import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryDTO;
import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryJson;
import com.cwallet.champwallet.models.budget.BudgetAllocationHistory;

public class BudgetAllocationHistoryMapper {
    public static BudgetAllocationHistory mapToBudgetAllocationHistory(BudgetAllocationHistoryDTO budgetAllocationHistoryDTO) {
        return BudgetAllocationHistory.builder()
                .id(budgetAllocationHistoryDTO.getId())
                .description(budgetAllocationHistoryDTO.getDescription())
                .amount(budgetAllocationHistoryDTO.getAmount())
                .wallet(budgetAllocationHistoryDTO.getWallet())
                .budget(budgetAllocationHistoryDTO.getBudget())
                .isAllocate(budgetAllocationHistoryDTO.getIsAllocate())
                .creationTime(budgetAllocationHistoryDTO.getCreationTime())
                .build();
    }

    public static BudgetAllocationHistoryDTO mapToBudgetAllocationHistoryDTO(BudgetAllocationHistory budgetAllocationHistory) {
        return BudgetAllocationHistoryDTO.builder()
                .id(budgetAllocationHistory.getId())
                .description(budgetAllocationHistory.getDescription())
                .amount(budgetAllocationHistory.getAmount())
                .wallet(budgetAllocationHistory.getWallet())
                .budget(budgetAllocationHistory.getBudget())
                .isAllocate(budgetAllocationHistory.getIsAllocate())
                .creationTime(budgetAllocationHistory.getCreationTime())
                .build();
    }
    public static BudgetAllocationHistoryJson mapToBudgetAllocationHistoryJson(BudgetAllocationHistory budgetAllocationHistory) {
        return BudgetAllocationHistoryJson.builder()
                .id(budgetAllocationHistory.getId())
                .description(budgetAllocationHistory.getDescription())
                .amount(budgetAllocationHistory.getAmount())
                .walletID(budgetAllocationHistory.getWallet().getId())
                .budgetID(budgetAllocationHistory.getBudget().getId())
                .isAllocate(budgetAllocationHistory.getIsAllocate())
                .creationTime(budgetAllocationHistory.getCreationTime())
                .build();
    }
}
