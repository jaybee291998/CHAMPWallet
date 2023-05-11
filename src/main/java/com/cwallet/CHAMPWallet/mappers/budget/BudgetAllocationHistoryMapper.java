package com.cwallet.CHAMPWallet.mappers.budget;

import com.cwallet.CHAMPWallet.dto.budget.BudgetAllocationHistoryDTO;
import com.cwallet.CHAMPWallet.models.budget.BudgetAllocationHistory;

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
}
