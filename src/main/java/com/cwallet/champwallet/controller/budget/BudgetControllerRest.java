package com.cwallet.champwallet.controller.budget;

import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryJson;
import com.cwallet.champwallet.dto.budget.BudgetJson;
import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryJson;
import com.cwallet.champwallet.repository.budget.BudgetTransferHistoryRepository;
import com.cwallet.champwallet.service.budget.BudgetAllocationHistoryService;
import com.cwallet.champwallet.service.budget.BudgetService;
import com.cwallet.champwallet.service.budget.BudgetTransferHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.budget.BudgetMapper.*;

@RestController
public class BudgetControllerRest {
    @Autowired
    private BudgetAllocationHistoryService budgetAllocationHistoryService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private BudgetTransferHistoryService budgetTransferHistoryService;

    @GetMapping("/users/api/budget/allocation-history/{budgetID}")
    List<BudgetAllocationHistoryJson> budgetAllocation(@PathVariable long budgetID) {
       List<BudgetAllocationHistoryJson> history = budgetAllocationHistoryService.budgetAllocationHistory(budgetID);
       return history;
    }
    @GetMapping("/users/api/budget/list")
    List<BudgetJson> allBudget() {
        return budgetService.getAllUserBudget().stream().map(e -> mapToBudgetJson(mapToBudget(e))).collect(Collectors.toList());
    }

    @GetMapping("/users/api/budget/budget-transferred-to/{senderBudgetID}")
    List<BudgetTransferHistoryJson> budgetTransferredTo(@PathVariable long senderBudgetID, @RequestParam int interval) {
        return budgetTransferHistoryService.budgetTransferredTo(senderBudgetID, interval);
    }

    @GetMapping("/users/api/budget/budget-received-from/{recipientBudgetID}")
    List<BudgetTransferHistoryJson> budgetReceivedFrom(@PathVariable long recipientBudgetID, @RequestParam int interval) {
        return budgetTransferHistoryService.budgetReceivedFrom(recipientBudgetID, interval);
    }
}
