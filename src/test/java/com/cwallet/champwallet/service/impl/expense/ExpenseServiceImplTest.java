package com.cwallet.champwallet.service.impl.expense;

import static com.cwallet.champwallet.mappers.budget.BudgetMapper.mapToBudgetDTO;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.BudgetDisabledException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.BudgetExpiredException;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.exception.expense.ExpenseExpiredException;
import com.cwallet.champwallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.models.income.Income;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.exception.expense.NoSuchExpenseOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetService;
import com.cwallet.champwallet.service.expense.ExpenseService;
import com.cwallet.champwallet.service.expenseType.ExpenseTypeService;
import com.cwallet.champwallet.service.impl.expense.ExpenseServiceImpl;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
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



    @Mock
    private ExpenseTypeService expenseTypeService;

    @Mock
    private BudgetService budgetService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        expenseService = expenseServiceImpl;
    }
    @Test
    public void testSaveIncome() throws NoSuchBudgetOrNotAuthorized, NoSuchEntityOrNotAuthorized, BudgetDisabledException, NoSuchExpenseTypeOrNotAuthorized, AccountingConstraintViolationException {
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

        List<ExpenseDTO> result = expenseServiceImpl.getAllUserExpenseAll();

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

    @org.junit.Test(expected = IllegalArgumentException.class)
    public void testUpdate_ShouldThrowIllegalArgumentException_WhenExpenseDTOIsNull() throws NoSuchExpenseOrNotAuthorized, NoSuchEntityOrNotAuthorized, ExpenseExpiredException, AccountingConstraintViolationException, NoSuchExpenseTypeOrNotAuthorized, BudgetDisabledException {
        ExpenseDTO expenseDTO = null;
        long expenseID = 1L;
        expenseService.update(expenseDTO, expenseID);
    }

//    @Test
//    void testUpdate() throws Exception {
//        UserEntity userEntity = new UserEntity();
//        Budget budget = new Budget();
//        budget.setId(1L);
//        budget.setBalance(1000.0);
//        Wallet wallet =new Wallet();
//        userEntity.setWallet(wallet);
//        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
//
//        Expense existingExpense = new Expense();
//        existingExpense .setId(1L);
//        existingExpense .setPrice(500.0);
//        when(expenseRepository.findByIdAndWalletId(1L, wallet.getId())).thenReturn(existingExpense );
//
//        ExpenseDTO updatedExpenseDTO = new ExpenseDTO();
//        updatedExpenseDTO.setDescription("Updated Description");
//        updatedExpenseDTO.setPrice(200.0);
////        updatedExpenseDTO.setExpenseType(new ExpenseType());
//        updatedExpenseDTO.setBudget(new Budget());
//
//        assertDoesNotThrow(() -> expenseServiceImpl.update(updatedExpenseDTO, 1L));
//
//        verify(securityUtil, times(2)).getLoggedInUser();
//        verify(expenseRepository, times(1)).findByIdAndWalletId(1L, wallet.getId());
//        verify(expenseRepository, times(1)).save(existingExpense);
//
//
//        assertEquals("Updated Description", existingExpense.getDescription());
//        assertEquals(200.0, existingExpense.getPrice());
//        assertEquals(700.0,budget.getBalance());
//
//    }
    @org.junit.Test
    public void testIsUpdateable_ShouldReturnFalse_WhenTheExpenseIsAlreadyExpired() throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException {
      ExpenseDTO expenseDTO = new ExpenseDTO();
        when(expirableAndOwnedService.isExpired(expenseDTO)).thenReturn(true);
        boolean isUpdateable = expenseService.isUpdateable(expenseDTO);
        assertFalse(isUpdateable);
        verify(expirableAndOwnedService).isExpired(expenseDTO);
    }
    @org.junit.Test
    public void testIsUpdateable_ShouldReturnFalse_WhenTheExpenseIsDisabled() throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException {
        ExpenseDTO expenseDTO =ExpenseDTO.builder()
                .price(100)
                .description("new description")
                .id(1L)
                .build();
        when(expirableAndOwnedService.isExpired(expenseDTO)).thenReturn(false);
        boolean isUpdateable = expenseService.isUpdateable(expenseDTO);
        assertFalse(isUpdateable);
        verify(expirableAndOwnedService).isExpired(expenseDTO);
    }

    }
