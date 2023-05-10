package com.cwallet.CHAMPWallet.controller.expenseType;

import com.cwallet.CHAMPWallet.bean.expenseType.ExpenseTypeForm;
import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import com.cwallet.CHAMPWallet.repository.expense.ExpenseRepository;
import com.cwallet.CHAMPWallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.expenseType.ExpenseTypeService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import com.cwallet.CHAMPWallet.utils.impl.ExpirableAndOwnedServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

import javax.validation.Valid;

@Controller
public class ExpenseTypeController {
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    private ExpenseTypeRepository expenseTypeRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

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
        ExpenseTypeDto expenseTypeDto = ExpenseTypeDto.builder()
                .name((expenseTypeForm.getName()))
                .description(expenseTypeForm.getDescription())
                .build();
        boolean success = expenseTypeService.save(expenseTypeDto);
        if(success){
            return "redirect:/users/expense-type/list";
        } else {
            model.addAttribute("expenseTypeForm", expenseTypeForm);
            return "redirect:/users/expense-type/create?failedtosave=failed to save the expense type";
        }
    }

    @GetMapping("/users/expense-type/list")
    public String getUsersExpenseType(Model model){
        List<ExpenseTypeDto> usersExpenseType = expenseTypeService.getAllUserExpenseType();

        model.addAttribute("usersExpenseType", usersExpenseType);
        return "expense-type-list";
    }

    @GetMapping("users/expense-type/{id}")
   public String getUsersExpenseTypeId(@PathVariable("id") long id, Model model){
        ExpenseTypeDto expenseTypeDto = null;
        try {
            expenseTypeDto = expenseTypeService.getExpenseTypeId(id);
        } catch (NoSuchExpenseTypeOrNotAuthorized e) {
            return "redirect:/users/expense-type/list?nosuchexpense-type=you are trying to access expense-type that doesn't exist";
        }
        boolean isExpired = expirableAndOwnedService.isExpired(expenseTypeDto);

        if(isExpired){
            model.addAttribute("buttonEnabled", false);
        }
        else{
            model.addAttribute("buttonEnabled", expenseRepository.findByExpenseTypeId(expenseTypeDto.getId()).isEmpty());
        }
        model.addAttribute("expenseType", expenseTypeDto);
        return "expense-type-details";
    }

    //
//    @GetMapping("users/expense-type/{id}/update")
//    public String editExpenseTypeDetails(@PathVariable("id") long id, Model model) throws NoSuchExpenseTypeOrNotAuthorized {
//            ExpenseTypeDto expenseTypeDto = expenseTypeService.getExpenseTypeId(id);
//            UserEntity loggedInUser = securityUtil.getLoggedInUser();
//            if(expenseTypeDto.getWallet().getId()){
//
//            }
//            return "redirect:/users/expense-type/list";
//    }

    @GetMapping("users/expense-type/{id}/update")
    public String editExpenseTypeDetails(@PathVariable("id") long id, Model model) throws NoSuchExpenseTypeOrNotAuthorized {
        ExpenseTypeDto expenseTypeDto;
        try {
            expenseTypeDto = expenseTypeService.getExpenseTypeId(id);
        } catch (NoSuchExpenseTypeOrNotAuthorized e) {
            return "redirect:/users/expense-type/list?nosuchexpense-type=you are trying to access expense-type that doesn't exist";
        }

        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        if (!expenseTypeDto.getWallet().getUser().equals(loggedInUser)) {
            throw new NoSuchExpenseTypeOrNotAuthorized("You have no rights to either access/use this resource");
        }

        model.addAttribute("expenseTypeForm", new ExpenseTypeForm());
        model.addAttribute("expenseType", expenseTypeDto);
        return "update-expense-type-form";
    }

    @PostMapping("users/expense-type/{id}/update")
    public String updateExpenseTypeDetails(@PathVariable("id") long id,
                                           @Valid @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                           BindingResult bindingResult, Model model) throws NoSuchExpenseTypeOrNotAuthorized {
        if (bindingResult.hasErrors()) {
            model.addAttribute("expenseTypeForm", expenseTypeForm);
            model.addAttribute("expenseType", expenseTypeService.getExpenseTypeId(id));
            return "update-expense-type-form";
        }

        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        ExpenseTypeDto expenseTypeDto = expenseTypeService.getExpenseTypeId(id);
        if (!expenseTypeDto.getWallet().getUser().equals(loggedInUser)) {
            throw new NoSuchExpenseTypeOrNotAuthorized("You have no rights to either access/use this resource");
        }

        expenseTypeDto.setName(expenseTypeForm.getName());
        expenseTypeDto.setDescription(expenseTypeForm.getDescription());

        expenseTypeService.updateExpenseType(expenseTypeDto);
        return "redirect:/users/expense-type/list";
    }
}
