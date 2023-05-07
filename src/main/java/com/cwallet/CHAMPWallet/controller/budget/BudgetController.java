package com.cwallet.CHAMPWallet.controller.budget;

import com.cwallet.CHAMPWallet.bean.budget.BudgetForm;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @GetMapping("/users/budget/create")
    public String getBudgetForm(Model model) {
        model.addAttribute("budgetForm", new BudgetForm());
        return "budget/budget-creation";
    }
}
