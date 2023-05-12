package com.cwallet.champwallet.controller.incomeType;

import com.cwallet.champwallet.bean.incomeType.IncomeTypeForm;
import com.cwallet.champwallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.champwallet.exception.EntityExpiredException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.incomeType.IncomeTypeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class IncomeTypeController {
    @Autowired
    private IncomeTypeService incomeTypeService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
//CREATE
    @GetMapping("/users/income-type/create")
    public String getIncomeTypeForm(Model model){
        model.addAttribute("incomeTypeForm", new IncomeTypeForm());
        return "create-income-type-form";
    }
    @PostMapping("/users/income-type/create")
    public String createIncomeTypeForm(@Valid @ModelAttribute("incomeTypeForm") IncomeTypeForm incomeTypeForm,
                                        BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println(incomeTypeForm);
            model.addAttribute("incomeTypeForm", incomeTypeForm);
            return "create-income-type-form";
        }
        IncomeTypeDto newIncomeType = IncomeTypeDto.builder()
                .name((incomeTypeForm.getName()))
                .description(incomeTypeForm.getDescription())
                .build();
        incomeTypeService.save(newIncomeType);

        return "redirect:/users/income-type/list";
    }
//    LIST
    @GetMapping("/users/income-type/list")
    public String getAllIncomeType(Model model) {
        List<IncomeTypeDto> incomeTypeList = incomeTypeService.getAllIncomeType();
        model.addAttribute("incomeTypeList",incomeTypeList);
        return "income-type-list";
    }
//DETAILS
    @GetMapping("/users/income-type/{id}")
    public String getIncomeTypeById(@PathVariable("id") long id, Model model) {
        IncomeTypeDto incomeTypeDto = null;
        try {
            incomeTypeDto = incomeTypeService.getIncomeTypeById(id);
        } catch (NoSuchEntityOrNotAuthorized e){
            return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type or unauthorized";
        }
        boolean isExpired = expirableAndOwnedService.isExpired(incomeTypeDto);
        IncomeType incomeType = null;
        try {
            incomeType = incomeTypeService.getIncomeType(id);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type or unauthorized";
        }
        if(isExpired) {
            model.addAttribute("isButtonEnabled", false);
        } else {
            model.addAttribute("isButtonEnabled", incomeType.getIncomes().isEmpty());
        }
        model.addAttribute("incomeType", incomeTypeDto);
        return "income-type-details";
    }
//    UPDATE
    @GetMapping("/users/income-type/update/{id}")
    public String getUpdateIncomeTypeForm(@PathVariable("id") long id, Model model) {
        IncomeTypeDto incomeTypeDto = null;
        try {
            incomeTypeDto = incomeTypeService.getIncomeTypeById(id);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type or unauthorized";
        }

        if(incomeTypeService.isUpdateable(incomeTypeDto)) {
            IncomeTypeForm incomeTypeForm = IncomeTypeForm.builder()
                    .id(incomeTypeDto.getId())
                    .name(incomeTypeDto.getName())
                    .description(incomeTypeDto.getDescription())
                    .build();
            model.addAttribute("incomeTypeForm", incomeTypeForm);
            return "income-type-update";
        } else {
            return "redirect:/users/income-type/list?nolongerupdateable=this income type is no longer updateable";
        }
    }
    @PostMapping("/users/income-type/update/{id}")
    public String updateIncomeType(@Valid @ModelAttribute("incomeTypeForm") IncomeTypeForm incomeTypeForm,
                               BindingResult bindingResult,
                               @PathVariable("id") long id, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("incomeTypeForm", incomeTypeForm);
            return "income-type/update";
        }
        IncomeTypeDto incomeTypeDto = null;
        try {
            incomeTypeDto = incomeTypeService.getIncomeTypeById(id);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type or unauthorized";
        }
        if(incomeTypeService.isUpdateable(incomeTypeDto)) {
            incomeTypeDto.setName(incomeTypeForm.getName());
            incomeTypeDto.setDescription(incomeTypeForm.getDescription());
            try {
                incomeTypeService.update(incomeTypeDto, id);
            } catch (NoSuchEntityOrNotAuthorized e) {
                return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type or unauthorized";
            }
            return String.format("redirect:/users/income-type/%s", id);
        } else {
            return "redirect:/users/income-type/list?nolongerupdateable=this income type is no longer updateable";
        }

    }
    //    DELETE

    @GetMapping("/users/income-type/delete/{id}")
    public String deleteConfirmation(@PathVariable ("id") long id, Model model){
        IncomeTypeDto incomeTypeDto = null;
        try{
            incomeTypeDto = incomeTypeService.getIncomeTypeById(id);
        }catch (NoSuchEntityOrNotAuthorized e){
            return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type  or unauthorized";
        }
        if(incomeTypeService.isUpdateable(incomeTypeDto)) {
            model.addAttribute("incomeType", incomeTypeDto);
            return "income-type-delete";
        } else {
            return "redirect:/users/income-type/list?nolongerupdateable=this income type  is no longer updateable";
        }
    }
    @GetMapping("/users/income-type/delete/confirmed/{id}")
    public String deleteIncomeType(@PathVariable("id") long id, Model model) {
        IncomeTypeDto incomeTypeDto = null;
        try {
            incomeTypeDto = incomeTypeService.getIncomeTypeById(id);
        } catch (NoSuchEntityOrNotAuthorized e) {
            return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type  or unauthorized";
        }
        if(incomeTypeService.isUpdateable(incomeTypeDto)) {
            try {
                incomeTypeService.deleteIncomeType(id);
            } catch (NoSuchEntityOrNotAuthorized e) {
                return "redirect:/users/income-type/list?NoSuchEntityOrNotAuthorized=no such income type  or unauthorized";
            } catch (EntityExpiredException e) {
                return "redirect:/users/income-type/list?nolongerupdateable=from service this income type  is no longer updateable";
            }
            return "redirect:/users/income-type/list?incomeTypedeleted=income type  successfully deleted";
        } else {
            return "redirect:/users/income-type/list?nolongerupdateable=from controller this income type  is no longer updateable";
        }
    }

}
