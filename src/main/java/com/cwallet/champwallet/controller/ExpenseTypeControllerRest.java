package com.cwallet.champwallet.controller;

import com.cwallet.champwallet.dto.ExpenseTypeJson;
import com.cwallet.champwallet.service.expenseType.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseType;
import static com.cwallet.champwallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseTypeJson;

@RestController
public class ExpenseTypeControllerRest {
    @Autowired
    private ExpenseTypeService expenseTypeService;

    @GetMapping("/users/api/expense-type/list")
    List<ExpenseTypeJson> getExpenseUserTypes() {
        return expenseTypeService.getAllUserExpenseType().stream().map(e -> mapToExpenseTypeJson(mapToExpenseType(e))).collect(Collectors.toList());
    }

}
