package com.cwallet.CHAMPWallet.controller.budget;

import com.cwallet.CHAMPWallet.bean.budget.BudgetForm;
import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.jws.WebParam;
import javax.validation.Valid;

@Controller
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @GetMapping("/users/budget/create")
    public String getBudgetForm(Model model) {
        model.addAttribute("budgetForm", new BudgetForm());
        return "budget/budget-creation";
    }

    @PostMapping("/users/budget/create")
    public String createBudget(@Valid @ModelAttribute("budgetForm") BudgetForm budgetForm,
                               BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("budgetForm", budgetForm);
            return "budget/budget-creation";
        }
        BudgetDTO budgetDTO = BudgetDTO.builder()
                .name(budgetForm.getName())
                .description(budgetForm.getDescription())
                .build();

        boolean success = budgetService.save(budgetDTO);
        if(success) {
            return "redirect:/users/home?budgetsavesuccess=budget saved succesfully";
        } else {
            model.addAttribute("budgetForm", budgetForm);
            return "redirect:/users/budget/create?failedtosave=failed to save the budget";
        }
    }
}
