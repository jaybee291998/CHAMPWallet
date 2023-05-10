package com.cwallet.CHAMPWallet.controller.income;

import com.cwallet.CHAMPWallet.bean.expenseType.ExpenseTypeForm;
import com.cwallet.CHAMPWallet.bean.income.IncomeForm;
import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.income.Income;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import com.cwallet.CHAMPWallet.service.income.IncomeService;
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
public class IncomeController {
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/users/income/create")
    public String getIncomeForm(Model model){
        model.addAttribute("incomeForm", new IncomeForm());
        model.addAttribute("incomeTypes", securityUtil.getLoggedInUser().getWallet().getIncomeTypes());
        return "income/add-income";
    }

    @PostMapping("/users/income/create")
    public String createIncomeForm(@Valid @ModelAttribute("incomeForm") IncomeForm incomeForm,
                                        BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            System.out.println(incomeForm);
            model.addAttribute("incomeForm", incomeForm);
            return "income/add-income";
        }
       IncomeDTO newIncome = IncomeDTO.builder()
               .source(incomeForm.getSource())
               .amount(incomeForm.getAmount())
               .description(incomeForm.getDescription())
                               .build();
        System.out.println("sadada"+newIncome);
        incomeService.save(newIncome, incomeForm.getIncomeTypeID());
        return "redirect:/users/home";
    }
//    @GetMapping("/user/income/delete")
//    public String deleteIncome(@PathVariable("income_id") long incomeId){
//        IncomeDto incomeDto = incomeService.
//        if(incomeDto == null) return "redirect:/income?nosuchevent=your trying to delete an income that doesnt exist";
//        UserEntity loggedInUser = securityUtil.getLoggedInUser();
//        if(!incomeDto.get){
//            return "redirect:/events?unauthorized=your trying to delete an event that's not your";
//        }
//        incomeService.deleteIncome(incomeId);
//        return "redirect:/users/home";
//    }
    @GetMapping("/users/income/list")
    public String getUsersBudget(Model model) {
        List<IncomeDTO> userIncome = incomeService.getAllUserIncome();
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        double totalAmount = userIncome.stream().reduce(0D, (subtotal, element) -> subtotal + element.getAmount(), Double::sum);
model.addAttribute("userIncome",userIncome);
model.addAttribute("totalAmount",totalAmount );
        return "income/income-list";
    }

}
