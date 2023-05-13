package com.cwallet.champwallet.bean.Expense;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.expense.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ExpenseForm {
    private long id;
    @NotNull
    @Size(min=5, max=2048)
    private String description;
    @NotNull
    private double price;
    private Budget budgetID;
    private ExpenseType expenseTypeID;

}
