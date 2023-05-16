package com.cwallet.champwallet.service.impl;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.BudgetDisabledException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.BudgetExpiredException;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.budget.BudgetAllocationHistory;
import com.cwallet.champwallet.models.budget.BudgetTransferHistory;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.budget.BudgetAllocationHistoryRepository;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetTransferHistoryService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static com.cwallet.champwallet.mappers.budget.BudgetMapper.mapToBudget;
import static com.cwallet.champwallet.mappers.budget.BudgetMapper.mapToBudgetDTO;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BudgetServiceImpl {
    @Mock
    private BudgetRepository budgetRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private BudgetAllocationHistoryRepository budgetAllocationHistoryRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private BudgetTransferHistoryService budgetTransferHistoryService;
    @InjectMocks
    private com.cwallet.champwallet.service.impl.budget.BudgetServiceImpl budgetService;
    @Test
    public void testSave_ShouldReturnTrue() {
        String budgetName = "budget name";
        String budgetDescription = "budget description";
        BudgetDTO budgetDTO = BudgetDTO.builder()
                .name(budgetName)
                .description(budgetDescription)
                .build();
        Wallet wallet = new Wallet();
        UserEntity user = UserEntity.builder()
                .wallet(wallet)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(user);
        boolean result = budgetService.save(budgetDTO);
        assertTrue(result);
        BudgetDTO modifiedDTO = BudgetDTO.builder()
                .name(budgetName)
                .description(budgetDescription)
                .wallet(wallet)
                .isEnabled(true)
                .balance(0)
                .build();
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).save(mapToBudget(modifiedDTO));
    }
    @Test(expected = NoSuchBudgetOrNotAuthorized.class)
    public void testGetSpecificBudget_ShouldThrowNoSuchBudgetOrNotAuthorized_WhenBudgetDoesntExistOrOwnedBySomebody() throws NoSuchBudgetOrNotAuthorized {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        long budgetID = 1L;
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(null);
        budgetService.getSpecificBudget(budgetID);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test
    public void testGetSpecificBudget_ShouldSucceed() throws NoSuchBudgetOrNotAuthorized {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = new Budget();
        long budgetID = 1L;
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        BudgetDTO budgetDTO = budgetService.getSpecificBudget(budgetID);
        assertEquals(budgetDTO, mapToBudgetDTO(budget));
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_ShouldThrowIllegalArgumentException_WhenBudgetDTOIsNull() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException {
        BudgetDTO budgetDTO = null;
        long budegtID = 1L;
        budgetService.update(budgetDTO, budegtID);
    }
    @Test(expected = NoSuchBudgetOrNotAuthorized.class)
    public void testUpdate_ShouldThrowNoSuchBudgetOrUnauthorized_WhenNoBudgetIsFoundOrTheBudgetIsNotOwnedByTheUser() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException {
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        BudgetDTO budgetDTO = new BudgetDTO();
        long budgetID = 1L;
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(null);
        budgetService.update(budgetDTO, budgetID);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = BudgetExpiredException.class)
    public void testUpdate_ShouldThrowBudgetExpiredException_TheBudgetIsAlreadyExpired() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException {
        LocalDateTime creationTime = LocalDateTime.now().minusHours(25);
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(1L)
                .creationTime(creationTime)
                .isEnabled(true)
                .build();
        BudgetDTO budgetDTO = BudgetDTO.builder()
                .build();
        long budgetID = 1L;
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(expirableAndOwnedService.isExpired(mapToBudgetDTO(budget))).thenReturn(true);
        budgetService.update(budgetDTO, budgetID);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test
    public void testUpdate_ShouldSucceed() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException {
        String budgetName = "budget_name";
        String budgetDescription = "budget description";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(1L)
                .isEnabled(true)
                .build();
        BudgetDTO budgetDTO = BudgetDTO.builder()
                .name(budgetName)
                .description(budgetDescription)
                .build();
        long budgetID = 1L;
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(expirableAndOwnedService.isExpired(mapToBudgetDTO(budget))).thenReturn(false);
        when(expenseRepository.findByBudgetId(budget.getId())).thenReturn(new ArrayList<>());
        budgetService.update(budgetDTO, budgetID);
        budget.setName(budgetName);
        budget.setDescription(budgetDescription);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
        verify(budgetRepository).save(budget);
    }
    @Test
    public void testIsUpdateable_ShouldReturnFalse_WhenTheBudgetIsAlreadyExpired() {
        BudgetDTO budgetDTO = new BudgetDTO();
        when(expirableAndOwnedService.isExpired(budgetDTO)).thenReturn(true);
        boolean isUpdateable = budgetService.isUpdateable(budgetDTO);
        assertFalse(isUpdateable);
        verify(expirableAndOwnedService).isExpired(budgetDTO);
    }
    @Test
    public void testIsUpdateable_ShouldReturnFalse_WhenTheBudgetIsDisabled() {
        BudgetDTO budgetDTO = BudgetDTO.builder()
                .isEnabled(false)
                .build();
        when(expirableAndOwnedService.isExpired(budgetDTO)).thenReturn(false);
        boolean isUpdateable = budgetService.isUpdateable(budgetDTO);
        assertFalse(isUpdateable);
        verify(expirableAndOwnedService).isExpired(budgetDTO);
    }
    @Test
    public void testIsUpdateable_ShouldReturnFalse_WhenTheBudgetIsAlreadyAssociatedWithAnExpense() {
        BudgetDTO budgetDTO = BudgetDTO.builder()
                .id(1L)
                .isEnabled(true)
                .build();
        when(expirableAndOwnedService.isExpired(budgetDTO)).thenReturn(false);
        when(expenseRepository.findByBudgetId(budgetDTO.getId())).thenReturn(Arrays.asList(new Expense()));
        boolean isUpdateable = budgetService.isUpdateable(budgetDTO);
        assertFalse(isUpdateable);
        verify(expirableAndOwnedService).isExpired(budgetDTO);
        verify(expenseRepository).findByBudgetId(budgetDTO.getId());
    }
    @Test
    public void testIsUpdateable_ShouldReturnTrue_WhenTheBudgetIsNotYetExpiredAndNotDisabledAndNoExpenseIsAssociated() {
        BudgetDTO budgetDTO = BudgetDTO.builder()
                .id(1L)
                .isEnabled(true)
                .build();
        when(expirableAndOwnedService.isExpired(budgetDTO)).thenReturn(false);
        when(expenseRepository.findByBudgetId(budgetDTO.getId())).thenReturn(Arrays.asList());
        boolean isUpdateable = budgetService.isUpdateable(budgetDTO);
        assertTrue(isUpdateable);
        verify(expirableAndOwnedService).isExpired(budgetDTO);
        verify(expenseRepository).findByBudgetId(budgetDTO.getId());
    }
    @Test(expected = NoSuchBudgetOrNotAuthorized.class)
    public void testDelete_ShouldThrowNoSuchBudgetOrNotAuthorized_WhenTheProvidedBudgetIDDoesntExistOrNotOwnedByTheUser() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException, AccountingConstraintViolationException {
        long budgetID = 1L;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(null);
        budgetService.deleteBudget(budgetID);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = BudgetExpiredException.class)
    public void testDelete_ShouldThrowBudgetExpiredException_WhenTheBudgetIsAlreadyExpired() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException, AccountingConstraintViolationException {
        long budgetID = 1L;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = new Budget();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(expirableAndOwnedService.isExpired(mapToBudgetDTO(budget))).thenReturn(false);
        budgetService.deleteBudget(budgetID);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
        verify(expirableAndOwnedService).isExpired(mapToBudgetDTO(budget));
    }
    @Test(expected = AccountingConstraintViolationException.class)
    public void testDelete_ShouldThrowAccountingConstraintException_WhenTheBudgetIsAlreadyInvolveOnSomeTransaction() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException, AccountingConstraintViolationException {
        long budgetID = 1L;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(1L)
                .isEnabled(true)
                .incomingTransferHistory(Arrays.asList(new BudgetTransferHistory()))
                .outgoingTransferHistory(new ArrayList<>())
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(expirableAndOwnedService.isExpired(mapToBudgetDTO(budget))).thenReturn(false);
        when(expenseRepository.findByBudgetId(budget.getId())).thenReturn(Arrays.asList());
        budgetService.deleteBudget(budgetID);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
        verify(expirableAndOwnedService).isExpired(mapToBudgetDTO(budget));
        verify(expenseRepository).findByBudgetId(budget.getId());
    }
    @Test
    public void testDelete_ShouldSucceed() throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException, AccountingConstraintViolationException {
        long budgetID = 1L;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(0)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(1L)
                .isEnabled(true)
                .balance(69)
                .incomingTransferHistory(new ArrayList<>())
                .outgoingTransferHistory(new ArrayList<>())
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(expirableAndOwnedService.isExpired(mapToBudgetDTO(budget))).thenReturn(false);
        when(expenseRepository.findByBudgetId(budget.getId())).thenReturn(Arrays.asList());
        budgetService.deleteBudget(budgetID);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
        verify(expirableAndOwnedService).isExpired(mapToBudgetDTO(budget));
        verify(expenseRepository).findByBudgetId(budget.getId());
        verify(walletRepository).save(wallet);
        verify(budgetRepository).delete(budget);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testAllocateToBudget_ShouldThrowIllegalArgumentException_WhenAmountIsLessThanZero() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long budgetID = 1L;
        double amount = -69;
        String description = "some description";
        boolean isAllocate = true;
        budgetService.allocateToBudget(budgetID, amount, description, isAllocate);
    }
    @Test(expected = BudgetDisabledException.class)
    public void testAllocateToBudget_ShouldThrowBudgetDisabledException_WhenTheBudgetIsDisabled() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long budgetID = 1L;
        double amount = 69;
        String description = "some description";
        boolean isAllocate = true;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .id(1L)
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(budgetID)
                .isEnabled(false)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        budgetService.allocateToBudget(budgetID, amount, description, isAllocate);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = AccountingConstraintViolationException.class)
    public void testAllocation_ShouldThrowAccountingContraintViolationException_WhenTheAmountToAllocateIsGreaterThanTheWalletBalance() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long budgetID = 1L;
        double amount = 6969;
        String description = "some description";
        boolean isAllocate = true;
        double walletBalance = 69;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(walletBalance)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .id(1L)
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(budgetID)
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        budgetService.allocateToBudget(budgetID, amount, description, isAllocate);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test
    public void testAllocation_ShouldSucceed() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long budgetID = 1L;
        double amount = 69;
        String description = "some description";
        boolean isAllocate = true;
        double walletBalance = 6969;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(walletBalance)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .id(1L)
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(budgetID)
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        budgetService.allocateToBudget(budgetID, amount, description, isAllocate);
        BudgetAllocationHistory budgetAllocationHistory = BudgetAllocationHistory.builder()
                .isAllocate(isAllocate)
                .description(description)
                .wallet(wallet)
                .budget(budget)
                .amount(amount)
                .build();
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
        verify(budgetAllocationHistoryRepository).save(budgetAllocationHistory);
        verify(walletRepository).save(wallet);
        verify(budgetRepository).save(budget);
    }
    @Test(expected = AccountingConstraintViolationException.class)
    public void testDeallocation_ShouldThrowAccountingConstrainViolationException_WhenThenAmountToDeallocateToTheBudgetIsGreaterThanTheBudgetBalance() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long budgetID = 1L;
        double amount = 6969;
        String description = "some description";
        boolean isAllocate = false;
        double walletBalance = 69;
        double budgetBalance = 69;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(walletBalance)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .id(1L)
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(budgetID)
                .balance(budgetBalance)
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        budgetService.allocateToBudget(budgetID, amount, description, isAllocate);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test
    public void testDeallocation_ShouldSucceed() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long budgetID = 1L;
        double amount = 69;
        String description = "some description";
        boolean isAllocate = false;
        double walletBalance = 6969;
        double budgetBalance = 6969;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .balance(walletBalance)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .id(1L)
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .id(budgetID)
                .balance(budgetBalance)
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        budgetService.allocateToBudget(budgetID, amount, description, isAllocate);
        BudgetAllocationHistory budgetAllocationHistory = BudgetAllocationHistory.builder()
                .isAllocate(isAllocate)
                .description(description)
                .wallet(wallet)
                .budget(budget)
                .amount(amount)
                .build();
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
        verify(budgetAllocationHistoryRepository).save(budgetAllocationHistory);
        verify(walletRepository).save(wallet);
        verify(budgetRepository).save(budget);
    }
}
