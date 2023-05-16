//package com.cwallet.champwallet.service.impl.expense;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.cwallet.champwallet.dto.expense.ExpenseDTO;
//import com.cwallet.champwallet.exception.expense.ExpenseExpiredException;
//import com.cwallet.champwallet.models.budget.Budget;
//import com.cwallet.champwallet.models.expense.Expense;
//import com.cwallet.champwallet.models.expense.ExpenseType;
//import com.cwallet.champwallet.repository.budget.BudgetRepository;
//import com.cwallet.champwallet.repository.expense.ExpenseRepository;
//import com.cwallet.champwallet.exception.expense.NoSuchExpenseOrNotAuthorized;
//import com.cwallet.champwallet.models.account.UserEntity;
//import com.cwallet.champwallet.models.account.Wallet;
//import com.cwallet.champwallet.repository.expense.ExpenseRepository;
//import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
//import com.cwallet.champwallet.security.SecurityUtil;
//import com.cwallet.champwallet.service.expense.ExpenseService;
//import com.cwallet.champwallet.service.impl.expense.ExpenseServiceImpl;
//import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
//import junit.framework.Assert;
//import org.junit.Before;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.util.*;
//import java.time.LocalDateTime;
//
//import static org.mockito.Mockito.*;
//class ExpenseServiceImplTest {
//    private ExpenseService expenseService;
//    @Mock
//    private ExpenseDTO expenseDTO;
//    @Mock
//    private ExpenseRepository expenseRepository;
//    @Mock
//    private ExpenseTypeRepository expenseTypeRepository;
//
//    @Mock
//    private BudgetRepository budgetRepository;
//    @Mock
//    private SecurityUtil securityUtil;
//    @Mock
//    private ExpirableAndOwnedService expirableAndOwnedService;
//
//    @InjectMocks
//    private ExpenseServiceImpl expenseServiceImpl ;
//
//
//    private ExpenseType expenseType;
//
//    private Budget budget;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//
//        expenseService = new ExpenseService(expenseRepository, expenseTypeRepository, budgetRepository, securityUtil);
//
//        expenseDTO = new ExpenseDTO();
//        expenseDTO.setName("Test Expense");
//        expenseDTO.setPrice(10.0);
//
//        expenseType = new ExpenseType();
//        expenseType.setId(1L);
//        expenseType.setName("Test Expense Type");
//
//        budget = new Budget();
//        budget.setId(1L);
//        budget.setName("Test Budget");
//        budget.setBalance(100.0);
//    }
//
//    @Test
//    public void testSave() {
//        User user = new User();
//        Wallet wallet = new Wallet();
//        wallet.setUser(user);
//        user.setWallet(wallet);
//
//        when(securityUtil.getLoggedInUser()).thenReturn(user);
//        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));
//        when(expenseTypeRepository.findById(1L)).thenReturn(Optional.of(expenseType));
//
//        expenseService.save(expenseDTO, expenseType, budget);
//
//        verify(budgetRepository, times(1)).save(budget);
//        verify(expenseRepository, times(1)).save(any(Expense.class));
//    }
//
//}