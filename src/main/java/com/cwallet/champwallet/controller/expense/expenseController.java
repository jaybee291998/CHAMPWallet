package com.cwallet.champwallet.controller.expense;

import com.cwallet.champwallet.bean.Expense.ExpenseForm;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
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
    public String getExpenseForm(Model model){
        model.addAttribute("expenseForm", new ExpenseForm());
        model.addAttribute("expenseTypes", securityUtil.getLoggedInUser().getWallet().getExpenseTypes());
         model.addAttribute("budgets", securityUtil.getLoggedInUser().getWallet().getBudgets());
        return "expense/add-expense";
    }

    @PostMapping("/users/expense/create")
    public String createExpenseForm(@Valid @ModelAttribute("expenseForm") ExpenseForm expenseForm,
                                   BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            System.out.println(expenseForm);
            model.addAttribute("expenseForm", expenseForm);
            return "expense/add-expense";
        }
        ExpenseDTO newExpense = ExpenseDTO.builder()
                .price(expenseForm.getPrice())
                .description(expenseForm.getDescription())
                .build();
        expenseService.save(newExpense,expenseForm.getExpenseTypeID(),expenseForm.getBudgetID());


        return "redirect:/users/home";
}
    @GetMapping("/users/expense/list")
    public String getUsersExpense(Model model) {
        List<ExpenseDTO> userExpense = expenseService.getAllUserExpense();
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        double totalAmount = userExpense.stream().reduce(0D, (subtotal, element) -> subtotal + element.getPrice(), Double::sum);
        model.addAttribute("userExpense",userExpense);

        model.addAttribute("totalAmount",totalAmount );
        return "expense/expense-list";
    }
}
