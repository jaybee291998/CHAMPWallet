package com.cwallet.champwallet.controller.budget;

import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryJson;
import com.cwallet.champwallet.dto.budget.BudgetJson;
import com.cwallet.champwallet.service.budget.BudgetAllocationHistoryService;
import com.cwallet.champwallet.service.budget.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/users/api/budget/allocation-history/{budgetID}")
    List<BudgetAllocationHistoryJson> budgetAllocation(@PathVariable long budgetID) {
       List<BudgetAllocationHistoryJson> history = budgetAllocationHistoryService.budgetAllocationHistory(budgetID);
       return history;
    }

    @GetMapping("/users/api/budget/list")
    List<BudgetJson> allBudget() {
        return budgetService.getAllUserBudget().stream().map(e -> mapToBudgetJson(mapToBudget(e))).collect(Collectors.toList());
    }
}
