package com.cwallet.champwallet.mappers.expense;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;

import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.models.income.Income;

public class ExpenseMapper {
    public static Expense mapToExpense(ExpenseDTO expenseDTO) {
        return Expense.builder()
                .id(expenseDTO.getId())
                .description(expenseDTO.getDescription())
                .price(expenseDTO.getPrice())
                .creationTime(expenseDTO.getCreationTime())
                .wallet(expenseDTO.getWallet())
                .expenseType(expenseDTO.getExpenseType())
                .budget(expenseDTO.getBudget())
                .build();
    }

    public static ExpenseDTO mapToExpenseDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .price(expense.getPrice())
                .creationTime(expense.getCreationTime())
                .wallet(expense.getWallet())
                .expenseType(expense.getExpenseType())
                .budget(expense.getBudget())
                .build();
    }

    public static ExpenseJson mapToExpenseJson(Expense expense) {
        return ExpenseJson.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .price(expense.getPrice())
                .creationTime(expense.getCreationTime())
                .walletID(expense.getWallet().getId())
                .expenseTypeID(expense.getExpenseType().getId())
                .budgetID(expense.getBudget().getId())
                .build();
    }
}
