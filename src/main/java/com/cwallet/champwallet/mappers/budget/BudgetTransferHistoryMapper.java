package com.cwallet.champwallet.mappers.budget;

import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryJson;
import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryDTO;
import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryJson;
import com.cwallet.champwallet.models.budget.BudgetTransferHistory;

public class BudgetTransferHistoryMapper {
    public static BudgetTransferHistoryDTO mapToBudgetTransferHistoryDTO(BudgetTransferHistory budgetTransferHistory) {
        return BudgetTransferHistoryDTO.builder()
                .id(budgetTransferHistory.getId())
                .senderBudget(budgetTransferHistory.getSenderBudget())
                .recipientBudget(budgetTransferHistory.getRecipientBudget())
                .amount(budgetTransferHistory.getAmount())
                .description(budgetTransferHistory.getDescription())
                .creationTime(budgetTransferHistory.getCreationTime())
                .wallet(budgetTransferHistory.getWallet())
                .build();
    }

    public static BudgetTransferHistory mapToBudgetTransferHistory(BudgetTransferHistoryDTO budgetTransferHistoryDTO) {
        return BudgetTransferHistory.builder()
                .id(budgetTransferHistoryDTO.getId())
                .senderBudget(budgetTransferHistoryDTO.getSenderBudget())
                .recipientBudget(budgetTransferHistoryDTO.getRecipientBudget())
                .amount(budgetTransferHistoryDTO.getAmount())
                .description(budgetTransferHistoryDTO.getDescription())
                .creationTime(budgetTransferHistoryDTO.getCreationTime())
                .wallet(budgetTransferHistoryDTO.getWallet())
                .build();
    }

    public static BudgetTransferHistoryJson mapToBudgetTransferHistoryJson(BudgetTransferHistory budgetTransferHistory) {
        return BudgetTransferHistoryJson.builder()
                .id(budgetTransferHistory.getId())
                .senderBudgetID(budgetTransferHistory.getSenderBudget().getId())
                .recipientBudgetID(budgetTransferHistory.getRecipientBudget().getId())
                .amount(budgetTransferHistory.getAmount())
                .creationTime(budgetTransferHistory.getCreationTime())
                .walletID(budgetTransferHistory.getWallet().getId())
                .build();
    }
}
