package com.cwallet.CHAMPWallet.controller.income;

import com.cwallet.CHAMPWallet.bean.income.IncomeForm;
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
