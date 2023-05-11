package com.cwallet.champwallet.repository.expense;

import com.cwallet.champwallet.models.expense.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByBudgetId(long budgetID);
    List<Expense> findByExpenseTypeId(long expenseTypeID);
}
