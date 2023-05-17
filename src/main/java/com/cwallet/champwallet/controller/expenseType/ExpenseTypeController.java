package com.cwallet.champwallet.controller.expenseType;

import com.cwallet.champwallet.bean.expenseType.ExpenseTypeForm;
import com.cwallet.champwallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.champwallet.exception.EntityExpiredException;
import com.cwallet.champwallet.exception.expenseType.ExpenseTypeExpiredException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.expenseType.ExpenseTypeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/expense-type/list?NoSuchEntityOrNotAuthorized=you are trying to access expense-type that doesn't exist";
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


    @GetMapping("users/expense-type/update/{expenseTypeId}")
    public String getUpdateExpenseTypeForm(@PathVariable("expenseTypeId") long expenseTypeId, Model model) throws NoSuchEntityOrNotAuthorized {
        ExpenseTypeDto expenseTypeDto = null;
        try {
            expenseTypeDto = expenseTypeService.getExpenseTypeId(expenseTypeId);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/expense-type/list?NoSuchEntityOrNotAuthorized=you are trying to access expense-type that doesn't exist";
        }

        if(expenseTypeService.isUpdatable(expenseTypeDto)){
            ExpenseTypeForm expenseTypeForm = ExpenseTypeForm.builder()
                    .id(expenseTypeDto.getId())
                    .name(expenseTypeDto.getName())
                    .description(expenseTypeDto.getDescription())
                    .build();
            model.addAttribute("expenseTypeForm", expenseTypeForm);
            return "/expense-type-update";
        } else {
            return "redirect:/users/expense-type/list?nolongerupdateable=this budget is no longer updateable";
        }
    }

    @PostMapping("users/expense-type/update/{expenseTypeId}")
    public String updateExpenseTypeDetails(@PathVariable("expenseTypeId") long expenseTypeId,
                                           @Valid @ModelAttribute("expenseTypeForm") ExpenseTypeForm expenseTypeForm,
                                           BindingResult bindingResult,
                                           Model model){
        if (bindingResult.hasErrors()) {
            model.addAttribute("expenseTypeForm", expenseTypeForm);
            return "/expense-type-update";
        }
        ExpenseTypeDto expenseTypeDto = null;

        try{
            expenseTypeDto = expenseTypeService.getExpenseTypeId(expenseTypeId);
        } catch (NoSuchEntityOrNotAuthorized e){
            return "redirect:/users/expense-type/list?NoSuchEntityOrNotAuthorized=no such expense type or unauthorized";
        }

        if(expenseTypeService.isUpdatable(expenseTypeDto)){
            expenseTypeDto.setName(expenseTypeForm.getName());
            expenseTypeDto.setDescription(expenseTypeForm.getDescription());

            try{
                expenseTypeService.updateExpenseType(expenseTypeDto, expenseTypeId);
            } catch (NoSuchEntityOrNotAuthorized e){
                return "redirect:/users/expense-type/list?NoSuchEntityOrNotAuthorized=no such expense type or unauthorized";
            }
            return String.format("redirect:/users/expense-type/%s", expenseTypeId);
        } else{
            return "redirect:/users/expense-type/list?nolongerupdatable=no longer updatable";
        }
    }

    @GetMapping("users/expense-type/delete/{expenseTypeId}")
    public String confirmDeleteExpenseTypeDetails(@PathVariable("expenseTypeId") long expenseTypeId, Model model){
        ExpenseTypeDto expenseTypeDto = null;

        try {
            expenseTypeDto = expenseTypeService.getExpenseTypeId(expenseTypeId);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/expense-type/list?NoSuchEntityOrNotAuthorized=no such expense type or unauthorized";
        }

        if(expenseTypeService.isUpdatable(expenseTypeDto)){
            model.addAttribute("expenseType", expenseTypeDto);
            return "expense-type-delete";
        }
        else{
            return "redirect:/users/expense-type/list?nolongerupdatable=this expense type is no longer updatable";
        }

    }
    @GetMapping("users/expense-type/delete/confirmed/{expenseTypeId}")
    public String deleteExpenseTypeDetails(@PathVariable("expenseTypeId") long expenseTypeId){
        ExpenseTypeDto expenseTypeDto = null;

        try {
            expenseTypeDto = expenseTypeService.getExpenseTypeId(expenseTypeId);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/expense-type/list?NoSuchEntityOrNotAuthorized=no such expense type or unauthorized";
        }

        if(expenseTypeService.isUpdatable(expenseTypeDto)){
            try {
                expenseTypeService.deleteExpenseType(expenseTypeId);
            } catch (NoSuchEntityOrNotAuthorized e) {
                return "redirect:/users/expense-type/list?NoSuchEntityOrNotAuthorized=no such expense type or unauthorized";
            } catch (EntityExpiredException e) {
                return "redirect:/users/expense-type/list?nolongerupdatable=this expense type is no longer updatable";
            }
            return "redirect:/users/expense-type/list?expensetypedeleted=expense type deleted successfully";
        }
        else{
            return "redirect:/users/expense-type/list?nolongerupdatable=this expense type is no longer updatable";
        }

    }
}
