package com.cwallet.champwallet.service.budget;

import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryDTO;
import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryJson;
import com.cwallet.champwallet.dto.budget.BudgetJson;

import java.util.List;

public interface BudgetAllocationHistoryService {
    List<BudgetAllocationHistoryJson> budgetAllocationHistory(long budgetId);
}
