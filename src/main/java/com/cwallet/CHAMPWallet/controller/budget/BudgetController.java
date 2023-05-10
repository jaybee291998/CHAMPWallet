package com.cwallet.CHAMPWallet.controller.budget;

import com.cwallet.CHAMPWallet.bean.budget.BudgetForm;
import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.exception.budget.BudgetExpiredException;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.repository.expense.ExpenseRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.jws.WebParam;

import javax.validation.Valid;
import java.util.List;

@Controller
public class BudgetController {
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    private ExpenseRepository expenseRepository;
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
            return "redirect:/users/budget/list";
        } else {
            model.addAttribute("budgetForm", budgetForm);
            return "redirect:/users/budget/create?failedtosave=failed to save the budget";
        }
    }

    @GetMapping("/users/budget/list")
    public String getUsersBudget(Model model) {
        List<BudgetDTO> userBudgets = budgetService.getAllUserBudget();
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        double allocatedBalance = userBudgets.stream().reduce(0D, (subtotal, element) -> subtotal + element.getBalance(), Double::sum);
        double unallocatedBalance = loggedInUser.getWallet().getBalance();
        model.addAttribute("userBudgets", userBudgets);
        model.addAttribute("unallocatedBalance", unallocatedBalance);
        model.addAttribute("allocatedBalance", allocatedBalance);
        model.addAttribute("totalBalance", allocatedBalance + unallocatedBalance);

        return "budget/budget-list";
    }

    @GetMapping("/users/budget/{budgetID}")
    public String getSpecificBudget(@PathVariable("budgetID") long budgetID, Model model) {
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        model.addAttribute("budget", budgetDTO);
        boolean isExpired = expirableAndOwnedService.isExpired(budgetDTO);
        if(isExpired) {
            // if expired
            model.addAttribute("buttonEnabled", false);
        } else {
            // else not expired yet so check if its used
            model.addAttribute("buttonEnabled", expenseRepository.findByBudgetId(budgetDTO.getId()).isEmpty());
        }
        return "budget/budget-detail";
    }

    @GetMapping("/users/budget/update/{budgetID}")
    public String getUpdateBudgetForm(@PathVariable("budgetID") long budgetID, Model model) {
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }

        if(budgetService.isUpdateable(budgetDTO)) {
            BudgetForm budgetForm = BudgetForm.builder()
                    .id(budgetDTO.getId())
                    .name(budgetDTO.getName())
                    .description(budgetDTO.getDescription())
                    .build();
            model.addAttribute("budgetForm", budgetForm);
            return "budget/budget-update";
        } else {
            return "redirect:/users/budget/list?nolongerupdateable=this budget is no longer updateable";
        }
    }
    @PostMapping("/users/budget/update/{budgetID}")
    public String updateBudget(@Valid @ModelAttribute("budgetForm") BudgetForm budgetForm,
                               BindingResult bindingResult,
                               @PathVariable("budgetID") long budgetID, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("budgetForm", budgetForm);
            return "budget/budget-update";
        }
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        if(budgetService.isUpdateable(budgetDTO)) {
            budgetDTO.setName(budgetForm.getName());
            budgetDTO.setDescription(budgetForm.getDescription());
            try {
                budgetService.update(budgetDTO, budgetID);
            } catch (NoSuchBudgetOrNotAuthorized e) {
                return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
            }
            return String.format("redirect:/users/budget/%s", budgetID);
        } else {
            return "redirect:/users/budget/list?nolongerupdateable=this budget is no longer updateable";
        }
    }

    @GetMapping("/users/budget/delete/{budgetID}")
    public String deleteConfirmation(@PathVariable("budgetID") long budgetID, Model model) {
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        if(budgetService.isUpdateable(budgetDTO)) {
            model.addAttribute("budget", budgetDTO);
            return "budget/budget-delete";
        } else {
            return "redirect:/users/budget/list?nolongerupdateable=this budget is no longer updateable";
        }
    }

    @GetMapping("/users/budget/delete/confirmed/{budgetID}")
    public String deleteBudget(@PathVariable("budgetID") long budgetID, Model model) {
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        if(budgetService.isUpdateable(budgetDTO)) {
            try {
                budgetService.deleteBudget(budgetID);
            } catch (NoSuchBudgetOrNotAuthorized e) {
                return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
            } catch (BudgetExpiredException e) {
                return "redirect:/users/budget/list?nolongerupdateable=from service this budget is no longer updateable";
            }
            return "redirect:/users/budget/list?budgetdeleted=budget successfully deleted";
        } else {
            return "redirect:/users/budget/list?nolongerupdateable=from controller this budget is no longer updateable";
        }
    }
}
