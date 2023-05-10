package com.cwallet.CHAMPWallet.controller.incomeType;

import com.cwallet.CHAMPWallet.bean.incomeType.IncomeTypeForm;
import com.cwallet.CHAMPWallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.incomeType.IncomeTypeService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
public class IncomeTypeController {
    private IncomeTypeService incomeTypeService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    public IncomeTypeController(IncomeTypeService incomeTypeService){
        this.incomeTypeService = incomeTypeService;
    }

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
    @GetMapping("/users/income-type/list")
    public String getAllIncomeType(Model model) {
        List<IncomeTypeDto> incomeTypeList = incomeTypeService.getAllIncomeType();
        model.addAttribute("incomeTypeList",incomeTypeList);
        return "income-type-list";
    }

    @GetMapping("/users/income-type/{id}")
    public String getIncomeTypeById(@PathVariable("id") long id, Model model) {
        IncomeTypeDto incomeTypeDto = null;
        try {
            incomeTypeDto = incomeTypeService.getIncomeTypeById(id);
        } catch (NoSuchBudgetOrNotAuthorized e){
            return "redirect:/users/income-type/list?nosuchincometypeornauthorized=no such income type or unauthorized";
        }
        boolean isExpired = expirableAndOwnedService.isExpired(incomeTypeDto);
        IncomeType incomeType = null;
        try {
            incomeType = incomeTypeService.getIncomeType(id);
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return "redirect:/users/income-type/list?nosuchincometypeornauthorized=no such income type or unauthorized";
        }
        if(isExpired) {
            model.addAttribute("isButtonEnabled", false);
        } else {
            model.addAttribute("isButtonEnabled", incomeType.getIncomes().isEmpty());
        }
        model.addAttribute("incomeType", incomeTypeDto);
        return "income-type-details";
    }
}
