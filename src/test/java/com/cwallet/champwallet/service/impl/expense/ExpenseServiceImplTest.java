package com.cwallet.champwallet.service.impl.expense;

import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.expense.ExpenseService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ExpenseServiceImplTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseTypeRepository expenseTypeRepository;
    @Mock
    private ExpenseDTO expenseDTO;
    @Mock
    private ExpenseService expenseService;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private Budget budget;
    @InjectMocks
    private ExpenseServiceImpl expenseServiceImpl;
    @Test
    void save() {
        ExpenseType expenseType = new ExpenseType();
        Budget budget = new Budget();
        Optional<Budget> optional = Optional.ofNullable(budget);
        budgetRepository.save(budget);
        expenseTypeRepository.save(expenseType);

        Long expenseTypeId = expenseType.getId();
        Long budgetId = budget.getId();

        Optional<Budget> optionalBudget = budgetRepository.findById(budgetId);
        Optional<ExpenseType> optionalExpenseType = expenseTypeRepository.findById(expenseTypeId);

//        when()


        ExpenseDTO expenseDto = new ExpenseDTO();
        expenseDto.setId(1L);
        expenseDto.setBudget(new Budget());
        expenseDto.setExpenseType(new ExpenseType());
        expenseDto.setDescription("Save Description");
        expenseDto.setPrice(100);
        expenseDto.setCreationTime(LocalDateTime.now());
        expenseDto.setWallet(new Wallet());

        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);




        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

    }
}