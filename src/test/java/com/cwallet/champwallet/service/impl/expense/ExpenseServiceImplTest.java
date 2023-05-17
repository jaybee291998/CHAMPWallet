package com.cwallet.champwallet.service.impl.expense;

import static org.junit.jupiter.api.Assertions.*;

import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.exception.expense.ExpenseExpiredException;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.exception.expense.NoSuchExpenseOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.expense.ExpenseService;
import com.cwallet.champwallet.service.impl.expense.ExpenseServiceImpl;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
class ExpenseServiceImplTest {
    private ExpenseService expenseService;
    @Mock
    private ExpenseDTO expenseDTO;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseTypeRepository expenseTypeRepository;

    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Mock
    private WalletRepository walletRepository;
    @InjectMocks
    private ExpenseServiceImpl expenseServiceImpl ;


    private ExpenseType expenseType;

    private Budget budget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        expenseService = expenseServiceImpl;
    }
    @Test
    public void testSaveIncome() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(1L);
        expenseDTO.setDescription("Save Description");
        expenseDTO.setPrice(100);
        expenseDTO.setCreationTime(LocalDateTime.now());
        expenseDTO.setWallet(new Wallet());

        ExpenseType expenseType = new ExpenseType();
        expenseType.setId(1L);
        expenseType.setName("Save Name");
        expenseType.setDescription("Save Description");
        expenseType.setWallet(new Wallet());
        expenseType.setCreationTime(LocalDateTime.now());

        Budget budget =new Budget();
        budget.setId(1L);
        budget.setBalance(1000);
        budget.setEnabled(true);
        budget.setDescription(" save description");
        budget.setName("save name");
        budget.setCreationTime(LocalDateTime.now());
        budget.setWallet(new Wallet());

        UserEntity userEntity = new UserEntity();


        when(expenseTypeRepository.findById(expenseType.getId())).thenReturn(Optional.of(expenseType));
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(budgetRepository.findById(budget.getId())).thenReturn(Optional.of(budget));
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(expenseRepository.save(any(Expense.class))).thenReturn(new Expense());
       // when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        boolean result = expenseServiceImpl.save(expenseDTO, expenseType, budget);
        assertTrue(result);

        verify(expenseTypeRepository, times(1)).findById(expenseType.getId());
        verify(budgetRepository, times(1)).findById(budget.getId());
        verify(securityUtil).getLoggedInUser();
        verify(expenseRepository).save(any(Expense.class));
       // verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    public void getAllUserExpenseTest(){
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        List<Expense> usersIncome = new ArrayList<>();
        Expense expense1 = new Expense();
        expense1.setId(1L);
        expense1.setDescription("Description");
        expense1.setPrice(100.0);
        expense1.setCreationTime(LocalDateTime.now());
        usersIncome.add(expense1);
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setDescription("Description2");
        expense2.setPrice(200.0);
        expense2.setCreationTime(LocalDateTime.now());
        usersIncome.add(expense2);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(expenseRepository.findByWalletId(wallet.getId())).thenReturn(usersIncome);

        List<ExpenseDTO> result = expenseServiceImpl.getAllUserExpense();

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseRepository, times(1)).findByWalletId(wallet.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Description", result.get(0).getDescription());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Description2", result.get(1).getDescription());
    }
    @Test
    public void getSpecificExpenseTest() throws NoSuchExpenseOrNotAuthorized {

        long expenseId = 1L;

        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(1L);
        expenseDTO.setExpenseType(new ExpenseType());
        expenseDTO.setDescription("Description");
        expenseDTO.setPrice(100.0);
        expenseDTO.setBudget(new Budget());
        expenseDTO.setCreationTime(LocalDateTime.now());


       Expense expense = new Expense();
        expense.setId(1L);
        expense.setDescription("Test Income");
        expense.setPrice(100.0);
        expense.setCreationTime(LocalDateTime.now());
        expense.setExpenseType(new ExpenseType());
        expense.setBudget(new Budget());

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(expenseRepository.findByIdAndWalletId(expenseId, wallet.getId())).thenReturn(expense);

        ExpenseDTO result = expenseServiceImpl.getSpecificExpense(expenseId);

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseRepository, times(1)).findByIdAndWalletId(expenseId, wallet.getId());

        assertEquals(1L, result.getId());
        assertEquals("Test Income", result.getDescription());
        assertEquals(100.0, result.getPrice());
    }

    @Test
    public void getSpecificExpenseTest_InvalidExpense(){

        long expenseId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        when(expenseRepository.findByIdAndWalletId(expenseId, wallet.getId())).thenReturn(null);

        assertThrows(NoSuchExpenseOrNotAuthorized.class, () -> expenseServiceImpl.getSpecificExpense(expenseId));

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseRepository, times(1)).findByIdAndWalletId(expenseId, wallet.getId());
    }


}