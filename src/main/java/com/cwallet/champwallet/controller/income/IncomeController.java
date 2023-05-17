package com.cwallet.champwallet.controller.income;

import com.cwallet.champwallet.bean.income.IncomeForm;
import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.income.IncomeService;
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
public class IncomeController {
    @Autowired
    private IncomeService incomeService;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;

    @GetMapping("/users/income/create")
    public String getIncomeForm(Model model){
        model.addAttribute("incomeForm", new IncomeForm());
        model.addAttribute("incomeTypes", securityUtil.getLoggedInUser().getWallet().getIncomeTypes());
        return "income/add-income";
    }

    @PostMapping("/users/income/create")
    public String createIncomeForm(@Valid @ModelAttribute("incomeForm") IncomeForm incomeForm,
                                        BindingResult bindingResult, Model model){
        model.addAttribute("incomeForm", incomeForm);
        model.addAttribute("incomeTypes", securityUtil.getLoggedInUser().getWallet().getIncomeTypes());
        if(bindingResult.hasErrors()){
            System.out.println(incomeForm);
            return "income/add-income";
        }
        if(incomeForm.getAmount() <= 0) {
            model.addAttribute("errorMessage", "Amount must be greater than 0");
            return "income/add-income";
        }
       IncomeDTO newIncome = IncomeDTO.builder()
               .source(incomeForm.getSource())
               .amount(incomeForm.getAmount())
               .description(incomeForm.getDescription())
                               .build();
        try {
            incomeService.save(newIncome, incomeForm.getIncomeTypeID());
        } catch (AccountingConstraintViolationException e) {
            model.addAttribute("errorMessage", "Amount must be greater than 0");
            return "income/add-income";
        } catch (NoSuchEntityOrNotAuthorized e) {
            throw new RuntimeException(e);
        }
        return "redirect:/users/income/list";
    }

    @GetMapping("/users/income/list")
    public String getUsersIncome(Model model) {
        List<IncomeDTO> userIncome = incomeService.getAllUserIncome();
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        double totalAmount = userIncome.stream().reduce(0D, (subtotal, element) -> subtotal + element.getAmount(), Double::sum);
        model.addAttribute("userIncome",userIncome);

        model.addAttribute("totalAmount",totalAmount );
        return "income/income-list";
    }

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
            ;
            model.addAttribute("buttonEnabled", incomeService.isUpdateable(incomeDTO));
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
            model.addAttribute("incomeTypes", securityUtil.getLoggedInUser().getWallet().getIncomeTypes());
            return "income/income-update";
        } else {
            return "redirect:/users/income/list?nolongerupdateable=this budget is no longer updateable";
        }
    }
    @PostMapping("/users/income/update/{incomeID}")
    public String updateIncome(@Valid @ModelAttribute("incomeForm") IncomeForm incomeForm,
                               BindingResult bindingResult,
                               @PathVariable("incomeID") long incomeID, Model model) {
        model.addAttribute("incomeForm", incomeForm);
        model.addAttribute("incomeTypes", securityUtil.getLoggedInUser().getWallet().getIncomeTypes());
        if(bindingResult.hasErrors()) {
            return "income/income-update"; //html pass
        }
        if(incomeForm.getAmount() <= 0) {
            model.addAttribute("errorMessage", "Amount must be greater than 0");
            return "income/income-update";
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
            } catch (NoSuchIncomeOrNotAuthorized | IncomeExpiredException e) {
                return "redirect:/users/income/list?nosuchincomeornauthorized=no such income or unauthorized";
            }catch (AccountingConstraintViolationException e){
                model.addAttribute("errorMessage", e.getMessage());
                return "income/income-update";
            }
            return String.format("redirect:/users/income/%s", incomeID);
        } else {
            return "redirect:/users/income/list?nolongerupdateable=this income is no longer updateable";
        }

    }

    @GetMapping("/users/income/delete/{incomeID}")
    public String deleteConfirmation(@PathVariable("incomeID") long incomeID, Model model) {
        IncomeDTO incomeDTO = null;
        try {
            incomeDTO = incomeService.getSpecificIncome(incomeID);
        } catch (NoSuchIncomeOrNotAuthorized e) {
            return "redirect:/users/income/list?nosuchincomeornauthorized=no such income or unauthorized";
        }
        if(incomeService.isUpdateable(incomeDTO)) {
            model.addAttribute("income", incomeDTO);
            return "income/income-delete";
        } else {
            return "redirect:/users/income/list?nolongerupdatable=this income is no longer updatable";
        }
    }

    @GetMapping("/users/income/delete/confirmed/{incomeID}")
    public String deleteIncome(@PathVariable("incomeID") long incomeID) {
        IncomeDTO incomeDTO = null;
        try {
            incomeDTO = incomeService.getSpecificIncome(incomeID);
        } catch (NoSuchIncomeOrNotAuthorized e) {
            return "redirect:/users/income/list?nosuchincomeornauthorized=no such income or unauthorized";
        }
        if(incomeService.isUpdateable(incomeDTO)) {
            try {
               incomeService.deleteIncome(incomeID);

            } catch (NoSuchIncomeOrNotAuthorized e) {
                return "redirect:/users/income/list?nosuchincomeorunauthorized=no such income or unauthorized";
            } catch (IncomeExpiredException e) {
                return "redirect:/users/income/list?nolongerupdateable=from service this income is no longer updatable";
            } catch (AccountingConstraintViolationException e) {
                return String.format("redirect:/users/income/%s?nolongerdeleatable=%s", incomeID, e.getMessage());
            }
            return "redirect:/users/income/list?incomedeleted=income successfully deleted";
        } else {
            return "redirect:/users/income/list?nolongerupdatable=from controller this income is no longer updatable";
        }
    }

//    @GetMapping("/data")
//    public String getData(Model model) {
//        List<> dataList = IncomeService.getAllData();
//        model.addAttribute("dataList", dataList);
//        return "data";
    }



