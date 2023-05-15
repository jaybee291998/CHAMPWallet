package com.cwallet.champwallet.controller;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.service.expense.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ExpenseControllerRest {
    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/users/api/expense/stats")
    List<ExpenseJson> expenseStats(@RequestParam int interval) {
        return expenseService.getExpensesWithinInterval(interval);
    }
}
