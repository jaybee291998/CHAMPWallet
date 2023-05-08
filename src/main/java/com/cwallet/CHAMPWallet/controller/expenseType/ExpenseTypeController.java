package com.cwallet.CHAMPWallet.controller.expenseType;

import com.cwallet.CHAMPWallet.bean.expenseType.ExpenseTypeForm;
import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import com.cwallet.CHAMPWallet.service.expenseType.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.validation.Valid;

@Controller
public class ExpenseTypeController {
    private ExpenseTypeService expenseTypeService;

    @Autowired
    public ExpenseTypeController(ExpenseTypeService expenseTypeService) {
        this.expenseTypeService = expenseTypeService;
    }

    @GetMapping("/users/expense-type/create")
    public String getExpenseTypeForm(Model model){
        model.addAttribute("expenseTypeForm", new ExpenseTypeForm());
        return "create-expense-type-form";
    }

    @PostMapping("/users/expense-type/create")
    public String createExpenseTypeForm(@Valid @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                        BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            System.out.println(expenseTypeForm);
            model.addAttribute("expenseTypeForm", expenseTypeForm);
            return "create-expense-type-form";
        }
        ExpenseTypeDto newExpense = ExpenseTypeDto.builder()
                .name((expenseTypeForm.getName()))
                .description(expenseTypeForm.getDescription())
                .build();
        expenseTypeService.save(newExpense);

        return "redirect:/users/home";
    }

    //New
    @GetMapping("/users/expense-type/list")
    public String expenseTypeDetail(Model model){
//        List<ExpenseType> expenseTypeList = expenseTypeService.findAll();
//        model.addAttribute("expenseTypeList", getExpenseTypeForm());
        return "expense-type-list";
    }
}
