package com.cwallet.champwallet.controller.budget;

import com.cwallet.champwallet.bean.BudgetAllocationForm;
import com.cwallet.champwallet.bean.BudgetTransferForm;
import com.cwallet.champwallet.bean.budget.BudgetForm;
import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.BudgetExpiredException;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

//import javax.jws.WebParam;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

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
            } catch (AccountingConstraintViolationException e) {
                return String.format("redirect:/users/budget/%s?nolongerdeleteable=%s", budgetDTO.getId(), e.getMessage());
            }
            return "redirect:/users/budget/list?budgetdeleted=budget successfully deleted";
        } else {
            return "redirect:/users/budget/list?nolongerupdateable=from controller this budget is no longer updateable";
        }
    }

    @GetMapping("/users/budget/allocation/{budgetID}")
    public String getAllocationForm(@PathVariable("budgetID") long budgetID, Model model) {
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        model.addAttribute("allocationForm", new BudgetAllocationForm());
        model.addAttribute("budgetID", budgetDTO.getId());
        model.addAttribute("walletBalance", securityUtil.getLoggedInUser().getWallet().getBalance());
        model.addAttribute("budget", budgetDTO);
        return "budget/budget-allocation";
    }

    @PostMapping("/users/budget/allocation/{budgetID}")
    public String budgetAllocation(@Valid @ModelAttribute("allocationForm") BudgetAllocationForm allocationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   @PathVariable("budgetID") long budgetID) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("allocationForm", allocationForm);
            return "budget/budget-allocation";
        }
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        if(allocationForm.getAmount() < 0) {
            model.addAttribute("allocationForm", allocationForm);
            model.addAttribute("errorMessage", "amount cant be negative");
            model.addAttribute("walletBalance", securityUtil.getLoggedInUser().getWallet().getBalance());
            model.addAttribute("budget", budgetDTO);
            return "budget/budget-allocation";
        }
        boolean isAllocate = allocationForm.getType().equalsIgnoreCase("allocate");
        try {
            budgetService.allocateToBudget(budgetID, allocationForm.getAmount(), allocationForm.getDescription(), isAllocate);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        } catch (AccountingConstraintViolationException e) {
            model.addAttribute("allocationForm", allocationForm);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("walletBalance", securityUtil.getLoggedInUser().getWallet().getBalance());
            model.addAttribute("budget", budgetDTO);
            return "budget/budget-allocation";
        }
        return String.format("redirect:/users/budget/%s", budgetID);
    }

    @GetMapping("/users/budget/allocation-history/{budgetID}")
    public String allocationHistory(@PathVariable Long budgetID, Model model) {
        model.addAttribute("budgetID", budgetID);
        return "budget/budget-allocation-history";
    }

    @GetMapping("/users/budget/fund-transfer/{budgetID}")
    public String getFundTransferForm(@PathVariable Long budgetID, Model model) {
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        model.addAttribute("budget", budgetDTO);
        model.addAttribute("transferForm", new BudgetTransferForm());
        model.addAttribute("budgetBalance", budgetDTO.getBalance());
        model.addAttribute("recipientBudgets", budgetService.getAllUserBudget().stream().filter(budget -> budget.getId() != budgetID).collect(Collectors.toList()));
        return "budget/budget-transfer";
    }

    @PostMapping("/users/budget/fund-transfer/{budgetID}")
    public String transferFund(@Valid @ModelAttribute("transferForm") BudgetTransferForm transferForm, BindingResult bindingResult,
                               Model model, @PathVariable Long budgetID) {
        BudgetDTO budgetDTO = null;
        try {
            budgetDTO = budgetService.getSpecificBudget(budgetID);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/budget/list?nosuchbudgetornauthorized=no such budget or unauthorized";
        }
        if(bindingResult.hasErrors()) {
            model.addAttribute("budget", budgetDTO);
            model.addAttribute("transferForm", transferForm);
            model.addAttribute("recipientBudgets", budgetService.getAllUserBudget().stream().filter(budget -> budget.getId() != budgetID && budget.isEnabled()).collect(Collectors.toList()));
            return  "budget/budget-transfer";
        }
        try {
            budgetService.fundTransferToOtherBudget(budgetID, transferForm.getRecipientBudgetID(), transferForm.getDescription(), transferForm.getAmount());
            return String.format("redirect:/users/budget/%s", budgetID);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return String.format("redirect:/users/budget/list?nosuchbudgetornauthorized=%s", e.getMessage());
        } catch (AccountingConstraintViolationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("transferForm", transferForm);
            model.addAttribute("budget", budgetDTO);
            model.addAttribute("recipientBudgets", budgetService.getAllUserBudget().stream().filter(budget -> budget.getId() != budgetID && budget.isEnabled()).collect(Collectors.toList()));
            return "budget/budget-transfer";
        }
    }
}
