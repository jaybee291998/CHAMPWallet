package com.cwallet.champwallet.controller.income;

import com.cwallet.champwallet.dto.income.IncomeJson;

import com.cwallet.champwallet.service.income.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
public class IncomeControllerRest {
    @Autowired
    private IncomeService incomeService;

    @GetMapping("/users/api/income/stats")
    List<IncomeJson> incomeStats(@RequestParam int interval) {
        return incomeService.getIncomeWithinInterval(interval);
    }

    @GetMapping("/users/api/income/statsmonth")
    List<IncomeJson> incomeStatus() {
        return incomeStats(30);
    }

}
