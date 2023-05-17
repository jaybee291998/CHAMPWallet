package com.cwallet.champwallet.controller.expense;

import com.cwallet.champwallet.bean.Expense.ExpenseForm;
import com.cwallet.champwallet.bean.income.IncomeForm;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.BudgetDisabledException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.exception.expense.ExpenseExpiredException;
import com.cwallet.champwallet.exception.expense.NoSuchExpenseOrNotAuthorized;
import com.cwallet.champwallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.expense.ExpenseService;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//import javax.jws.WebParam;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class expenseController {
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;


    @GetMapping("/users/expense/create")
    public String getExpenseForm(Model model) {
        model.addAttribute("expenseForm", new ExpenseForm());
        model.addAttribute("expenseTypes", securityUtil.getLoggedInUser().getWallet().getExpenseTypes());
        model.addAttribute("budgets", securityUtil.getLoggedInUser().getWallet().getBudgets().stream().filter(b -> b.isEnabled()).collect(Collectors.toList()));
        return "expense/add-expense";
    }

    @PostMapping("/users/expense/create")
    public String createExpenseForm(@Valid @ModelAttribute("expenseForm") ExpenseForm expenseForm,
                                    BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println(expenseForm);
            model.addAttribute("expenseForm", expenseForm);
            return "expense/add-expense";
        }
        model.addAttribute("expenseForm", expenseForm);
        model.addAttribute("expenseTypes", securityUtil.getLoggedInUser().getWallet().getExpenseTypes());
        model.addAttribute("budgets", securityUtil.getLoggedInUser().getWallet().getBudgets().stream().filter(b -> b.isEnabled()).collect(Collectors.toList()));
        if(expenseForm.getPrice() <= 0) {
            model.addAttribute("errorMessage", "price must be greater than 0");
            return "expense/add-expense";
        }
        ExpenseDTO newExpense = ExpenseDTO.builder()
                .price(expenseForm.getPrice())
                .description(expenseForm.getDescription())
                .build();
        try {
            boolean res = expenseService.save(newExpense, expenseForm.getExpenseTypeID(), expenseForm.getBudgetID());
            if(res) {
                return String.format("redirect:/users/expense/list?savesuccess=%s", "expense saved successfully");
            }
            return String.format("redirect:/users/expense/list?failedtosave=%s", "failed to save expense");
        } catch (NoSuchEntityOrNotAuthorized e) {
            return String.format("redirect:/users/expense/list?failedtosave=%s", e.getMessage());
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return String.format("redirect:/users/expense/list?failedtosave=%s", e.getMessage());
        } catch (NoSuchExpenseTypeOrNotAuthorized e) {
            return String.format("redirect:/users/expense/list?failedtosave=%s", e.getMessage());
        } catch (BudgetDisabledException e) {
            return String.format("redirect:/users/expense/list?failedtosave=%s", e.getMessage());
        } catch (AccountingConstraintViolationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "expense/add-expense";
        }
    }

   @GetMapping("/users/expense/list")
//
//    public String getUsersExpense(Model model) {
//        List<ExpenseDTO> userExpense = expenseService.getAllUserExpense();
//        UserEntity loggedInUser = securityUtil.getLoggedInUser();
//        double totalExpense = userExpense.stream().reduce(0D, (subtotal, element) -> subtotal + element.getPrice(), Double::sum);
//        model.addAttribute("userExpense", userExpense);
//
//        model.addAttribute("totalExpense", totalExpense);
//        return "income/expense-list";
//    }

    public String getUsersExpense(@RequestParam(value = "specific-date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate specificDate, Model model) {
        List<ExpenseDTO> userExpense = expenseService.getAllUserExpense(specificDate);
        double totalAmount = userExpense.stream().reduce(0D, (subtotal, element) -> subtotal + element.getPrice(), Double::sum);
        model.addAttribute("userExpense", userExpense);
        model.addAttribute("totalAmount", totalAmount);
        if(specificDate == null) {
            specificDate = LocalDate.now();
        }
        model.addAttribute("entryDate", specificDate);

        return "expense/expense-list";
    }

//    @GetMapping("/users/expense/list")
//    public String getUsersExpense(@RequestParam("specific-date") Optional<LocalDate> specificDate, Model model) {
//        if(specificDate.isPresent()) {
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~```");
//            System.out.println(specificDate.get());
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~```");
//        }
//        List<ExpenseDTO> userExpense = expenseService.getAllUserExpense();
//        UserEntity loggedInUser = securityUtil.getLoggedInUser();
//        double totalAmount = userExpense.stream().reduce(0D, (subtotal, element) -> subtotal + element.getPrice(), Double::sum);
//        model.addAttribute("userExpense", userExpense);
//
//        model.addAttribute("totalAmount", totalAmount);
//        return "expense/expense-list";
//    }
    @SneakyThrows
    @GetMapping("/users/expense/{expenseID}")
    public String getSpecificExpense(@PathVariable("expenseID") long expenseID, Model model) {
        ExpenseDTO expenseDTO = null;
        try {
            expenseDTO = expenseService.getSpecificExpense(expenseID);
        } catch (NoSuchExpenseOrNotAuthorized e) {
            return "redirect:/users/expense/list?nosuchexpenseornauthorized=no such expense or unauthorized";
        }
        model.addAttribute("expense", expenseDTO);
        boolean isExpired = expirableAndOwnedService.isExpired(expenseDTO);
        if (isExpired) {
            // if expired
            model.addAttribute("buttonEnabled", false);
        } else {
            // else not expired yet so check if its used
            ;
            model.addAttribute("buttonEnabled", expenseService.isUpdateable(expenseDTO));
        }
        return "expense/expense-detail";
    }

    @SneakyThrows
    @GetMapping("/users/expense/update/{expenseID}")
    public String getUpdateExpenseForm(@PathVariable("expenseID") long expenseID, Model model) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException {
        ExpenseDTO expenseDTO = null;
        try {
            expenseDTO = expenseService.getSpecificExpense(expenseID);
        } catch (NoSuchExpenseOrNotAuthorized e) {
            return "redirect:/users/expense/list?nosuchexpenseornauthorized=no such expense or unauthorized";
        }

        if (expenseService.isUpdateable(expenseDTO)) {
            ExpenseForm expenseForm = ExpenseForm.builder()
                    .id(expenseDTO.getId())
                    .description(expenseDTO.getDescription())
                    .expenseTypeID(expenseDTO.getExpenseType())
                    .price(expenseDTO.getPrice())
                    .budgetID(expenseDTO.getBudget())
                    .build();
            model.addAttribute("expenseForm", expenseForm);
            model.addAttribute("expenseTypes", securityUtil.getLoggedInUser().getWallet().getExpenseTypes());
            model.addAttribute("budgets", securityUtil.getLoggedInUser().getWallet().getBudgets().stream().filter(b -> b.isEnabled()).collect(Collectors.toList()));
            return "expense/expense-update";
        } else {
            return "redirect:/users/expense/list?nolongerupdateable=this expense is no longer updateable";
        }
    }

    @PostMapping("/users/expense/update/{expenseID}")
    public String updateExpense(@Valid @ModelAttribute("expenseForm") ExpenseForm expenseForm,
                                BindingResult bindingResult,
                                @PathVariable("expenseID") long expenseID, Model model) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("expenseForm", expenseForm);
            return "expense/expense-update"; //html pass
        }
        model.addAttribute("expenseForm", expenseForm);
        model.addAttribute("expenseTypes", securityUtil.getLoggedInUser().getWallet().getExpenseTypes());
        model.addAttribute("budgets", securityUtil.getLoggedInUser().getWallet().getBudgets().stream().filter(b -> b.isEnabled()).collect(Collectors.toList()));
        if(expenseForm.getPrice() <= 0) {
            model.addAttribute("errorMessage", "price must not be less than or equal to zero");
            return "expense/expense-update";
        }
        ExpenseDTO expenseDTO = null;
        try {
            expenseDTO = expenseService.getSpecificExpense(expenseID);
        } catch (NoSuchExpenseOrNotAuthorized e) {
            return "redirect:/users/expense/list?nosuchexpenseornauthorized=no such expense or unauthorized";
        }
        if (expenseService.isUpdateable(expenseDTO)) {
            expenseDTO.setDescription(expenseForm.getDescription());
            expenseDTO.setExpenseType(expenseForm.getExpenseTypeID());
            expenseDTO.setPrice(expenseForm.getPrice());
            expenseDTO.setBudget(expenseForm.getBudgetID());

            try {
                expenseService.update(expenseDTO, expenseID);
            } catch (NoSuchExpenseOrNotAuthorized | ExpenseExpiredException e) {
                return "redirect:/users/expense/list?nosuchexpenseornauthorized=no such expense or unauthorized";
            } catch (AccountingConstraintViolationException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "expense/expense-update";
            } catch (NoSuchEntityOrNotAuthorized e) {
                return "redirect:/users/expense/list?nosuchexpenseornauthorized=budget is not found";
            } catch (BudgetDisabledException e) {
                return String.format("redirect:/users/expense/%s?budgetdisabled=the budget is disabled", expenseID);
            } catch (NoSuchExpenseTypeOrNotAuthorized e) {
                return "redirect:/users/expense/list?nosuchexpenseornauthorized=no such expense type or unauthorized";
            }
            return String.format("redirect:/users/expense/%s", expenseID);
        } else {
            return "redirect:/users/expense/list?nolongerupdateable=this expense is no longer updateable";
        }

    }

    @GetMapping("/users/expense/delete/{expenseID}")
    public String deleteConfirmation(@PathVariable("expenseID") long expenseID, Model model) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException {
        ExpenseDTO expenseDTO = null;
        try {
            expenseDTO = expenseService.getSpecificExpense(expenseID);
        } catch (NoSuchExpenseOrNotAuthorized e) {
            return "redirect:/users/expense/list?nosuchexpenseornauthorized=no such expense or unauthorized";
        }
        if (expenseService.isUpdateable(expenseDTO)) {
            model.addAttribute("expense", expenseDTO);
            return "expense/expense-delete";
        } else {
            return "redirect:/users/expense/list?nolongerupdatable=this expense is no longer updatable";
        }
    }

    @GetMapping("/users/expense/delete/confirmed/{expenseID}")
    public String deleteExpense(@PathVariable("expenseID") long expenseID, RedirectAttributes redirectAttributes)  {
        try {
            ExpenseDTO expenseDTO = expenseService.getSpecificExpense(expenseID);
            if (expenseService.isUpdateable(expenseDTO)) {
                expenseService.deleteExpense(expenseID);
                redirectAttributes.addFlashAttribute("successMessage", "Expense successfully deleted!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "This expense is no longer updatable.");
            }
        } catch (NoSuchExpenseOrNotAuthorized e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No such expense or unauthorized.");
        } catch (ExpenseExpiredException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "This expense has expired.");
        } catch (NoSuchEntityOrNotAuthorized e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No such budget.");
        } catch (BudgetDisabledException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Budget Expired");
        }
        return "redirect:/users/expense/list";
    }



    @GetMapping("/users/expense/stats")
    public String getExpenseStats(Model model) {
        return "expense/expense-stats";
    }
}

