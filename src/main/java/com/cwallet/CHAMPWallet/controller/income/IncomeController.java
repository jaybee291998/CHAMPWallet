package com.cwallet.CHAMPWallet.controller.income;

import com.cwallet.CHAMPWallet.bean.budget.BudgetForm;
import com.cwallet.CHAMPWallet.bean.expenseType.ExpenseTypeForm;
import com.cwallet.CHAMPWallet.bean.income.IncomeForm;
import com.cwallet.CHAMPWallet.dto.budget.BudgetDTO;
import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.income.Income;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import com.cwallet.CHAMPWallet.repository.expense.ExpenseRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.budget.BudgetService;
import com.cwallet.CHAMPWallet.service.income.IncomeService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import com.cwallet.CHAMPWallet.repository.budget.BudgetRepository;

import javax.jws.WebParam;
import javax.validation.Valid;
import java.util.List;

@Controller
public class IncomeController {
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    private BudgetRepository budgetRepository;

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
        incomeService.save(newIncome, String.valueOf(incomeForm.getIncomeTypeID()));
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
    @SneakyThrows
    @GetMapping("/users/income/{incomeID}")
    public String getSpecificIncome(@PathVariable("incomeID") long incomeID, Model model) {
       IncomeDTO incomeDTO = null;
        try {
            incomeDTO = incomeService.getSpecificIncome(incomeID);
        } catch (NoSuchIncomeOrNotAuthorized e) {
            return "redirect:/users/income/list?nosuchincomeornauthorized=no such income or unauthorized";
        }
        model.addAttribute("income", incomeDTO);
        boolean isExpired = expirableAndOwnedService.isExpired(incomeDTO);
        if(isExpired) {
            // if expired
            model.addAttribute("buttonEnabled", false);
        } else {
            // else not expired yet so check if its used
            model.addAttribute("buttonEnabled", budgetRepository.findById(incomeDTO.getId()).isEmpty());
        }
        return "income/income-detail";
    }
    @GetMapping("/users/income/update/{incomeID}")
    public String getUpdateIncomeForm(@PathVariable("incomeID") long incomeID, Model model) {
        IncomeDTO incomeDTO = null;
        try {
            incomeDTO = incomeService.getSpecificIncome(incomeID);
        } catch (NoSuchIncomeOrNotAuthorized e) {
            return "redirect:/users/income/list?nosuchincomeornauthorized=no such income or unauthorized";
        }

        if(incomeService.isUpdateable(incomeDTO)) {
           IncomeForm incomeForm =IncomeForm.builder()
                   .id(incomeDTO.getId())
                   .description(incomeDTO.getDescription())
                   .source(incomeDTO.getSource())
                   .incomeTypeID(incomeDTO.getIncomeType())
                   .amount(incomeDTO.getAmount())
                    .build();
            model.addAttribute("incomeForm", incomeForm);
            return "income/income-update";
        } else {
            return "redirect:/users/income/list?nolongerupdateable=this budget is no longer updateable";
        }
    }
    @PostMapping("/users/budget/update/{incomeID}")
    public String updateIncome(@Valid @ModelAttribute("incomeForm") IncomeForm incomeForm,
                               BindingResult bindingResult,
                               @PathVariable("incomeID") long incomeID, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("incomeForm", incomeForm);
            return "income/income-update"; //html pass
        }
        IncomeDTO incomeDTO= null;
        try {
            incomeDTO = incomeService.getSpecificIncome(incomeID);
        } catch (NoSuchIncomeOrNotAuthorized e) {
            return "redirect:/users/income/list?nosuchincomeornauthorized=no such income or unauthorized";
        }
        if(incomeService.isUpdateable(incomeDTO)) {
            incomeDTO.setSource(incomeForm.getSource());
            incomeDTO.setDescription(incomeForm.getDescription());
            incomeDTO.setAmount(incomeForm.getAmount());
            incomeDTO.setIncomeType(incomeForm.getIncomeTypeID());


            try {
                incomeService.update(incomeDTO,incomeID);
            } catch (NoSuchIncomeOrNotAuthorized e) {
                return "redirect:/users/budget/list?nosuchincomeornauthorized=no such income or unauthorized";
            }
            return String.format("redirect:/users/income/%s", incomeID);
        } else {
            return "redirect:/users/income/list?nolongerupdateable=this income is no longer updateable";
        }

    }

}
