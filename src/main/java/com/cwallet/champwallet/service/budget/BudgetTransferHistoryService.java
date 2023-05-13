package com.cwallet.champwallet.service.budget;

import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryDTO;
import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryJson;

import java.util.List;

public interface BudgetTransferHistoryService {
    void save(BudgetTransferHistoryDTO budgetTransferHistoryDTO);
    List<BudgetTransferHistoryJson> budgetTransferredTo(long senderBudgetID, int intervalInDays);
    List<BudgetTransferHistoryJson> budgetReceivedFrom(long recipientBudgetID, int intervalInDays);
}
