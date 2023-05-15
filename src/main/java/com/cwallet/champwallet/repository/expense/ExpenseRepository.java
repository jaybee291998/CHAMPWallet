package com.cwallet.champwallet.repository.expense;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.models.income.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByBudgetId(long budgetID);
    List<Expense> findByExpenseTypeId(long expenseTypeID);
    Expense findByIdAndWalletId(long expenseID, long walletID);
    List<Expense> findByWalletId(Long walletID);
    @Query(
            value = "SELECT id, creation_time, description, price, budget_id, expense_type_id, wallet_id FROM " +
                    "expense WHERE creation_time BETWEEN :startDate AND :endDate AND wallet_id=:walletID",
            nativeQuery=true
    )
    List<Expense> getExpensesWithinDateRange(long walletID, LocalDateTime startDate, LocalDateTime endDate);
}
