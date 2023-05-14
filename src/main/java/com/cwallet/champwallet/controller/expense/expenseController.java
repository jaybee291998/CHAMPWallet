package com.cwallet.champwallet.controller.expense;

import com.cwallet.champwallet.bean.Expense.ExpenseForm;
import com.cwallet.champwallet.bean.income.IncomeForm;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.expense.ExpenseExpiredException;
import com.cwallet.champwallet.exception.expense.NoSuchExpenseOrNotAuthorized;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.expense.ExpenseService;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import com.cwallet.champwallet.repository.budget.BudgetRepository;

//import javax.jws.WebParam;
import javax.validation.Valid;
import java.util.List;

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
        model.addAttribute("budgets", securityUtil.getLoggedInUser().getWallet().getBudgets());
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
        ExpenseDTO newExpense = ExpenseDTO.builder()
                .price(expenseForm.getPrice())
                .description(expenseForm.getDescription())
                .build();
        expenseService.save(newExpense, expenseForm.getExpenseTypeID(), expenseForm.getBudgetID());


        return "redirect:/users/home";
    }

    @GetMapping("/users/expense/list")
    public String getUsersExpense(Model model) {
        List<ExpenseDTO> userExpense = expenseService.getAllUserExpense();
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        double totalAmount = userExpense.stream().reduce(0D, (subtotal, element) -> subtotal + element.getPrice(), Double::sum);
        model.addAttribute("userExpense", userExpense);

        model.addAttribute("totalAmount", totalAmount);
        return "expense/expense-list";
    }

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
            model.addAttribute("budget", securityUtil.getLoggedInUser().getWallet().getBudgets());
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
    public String deleteExpense(@PathVariable("expenseID") long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException {
        ExpenseDTO expenseDTO = null;
        try {
            expenseDTO = expenseService.getSpecificExpense(expenseID);
        } catch (NoSuchExpenseOrNotAuthorized e) {
            return "redirect:/users/expense/list?nosuchexpenseornauthorized=no such expense or unauthorized";
        }
        if (expenseService.isUpdateable(expenseDTO)) {
            try {
                expenseService.deleteExpense(expenseID);

            } catch (NoSuchExpenseOrNotAuthorized e) {
                return "redirect:/users/expense/list?nosuchexpenseorunauthorized=no such expense or unauthorized";
            } catch (ExpenseExpiredException e) {
                return "redirect:/users/expense/list?nolongerupdateable=from service this expense is no longer updatable";
            }
            return "redirect:/users/expense/list?expensedeleted=expense successfully deleted";
        } else {
            return "redirect:/users/expense/list?nolongerupdatable=from controller this expense is no longer updatable";
        }
    }
}