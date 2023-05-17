package com.cwallet.champwallet.service.impl;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryDTO;
import com.cwallet.champwallet.exception.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock
    private PasswordEncoder passwordEncoder;
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
    @Test(expected = IllegalArgumentException.class)
    public void testFundTransferToOtherBudget_ShouldThrowIllegalArgumentException_WhenDescriptionIsNull() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = null;
        double amount = 69;
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFundTransferToOtherBudget_ShouldThrowIllegalArgumentException_WhenDescriptionIsEmpty() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "";
        double amount = 69;
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFundTransferToOtherBudget_ShouldThrowIllegalArgumentException_WhenAmountIsLessThanOrEqualToZero() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "some description";
        double amount = -69;
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testFundTransferToOtherBudget_ShouldThrowIllegalArgumentException_WhenSenderBudgetAndRecipientBudgetIsTheSame() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 1L;
        String description = "some description";
        double amount = 69;
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
    }
    @Test(expected = NoSuchEntityOrNotAuthorized.class)
    public void testFundTransferToOtherBudget_ShouldThrowNoSuchEntityOrNotAuthorized_WhenSenderBudgetIsNull() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "some description";
        double amount = 69;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(senderBudgetID, wallet.getId())).thenReturn(null);
        when(budgetRepository.findByIdAndWalletId(recipientBudgetID, wallet.getId())).thenReturn(null);
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(senderBudgetID, wallet.getId());
        verify(budgetRepository).findByIdAndWalletId(recipientBudgetID, wallet.getId());
    }
    @Test(expected = NoSuchEntityOrNotAuthorized.class)
    public void testFundTransferToOtherBudget_ShouldThrowNoSuchEntityOrNotAuthorized_WhenRecipientBudgetIsNull() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "some description";
        double amount = 69;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget senderBudget = new Budget();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(senderBudgetID, wallet.getId())).thenReturn(senderBudget);
        when(budgetRepository.findByIdAndWalletId(recipientBudgetID, wallet.getId())).thenReturn(null);
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(senderBudgetID, wallet.getId());
        verify(budgetRepository).findByIdAndWalletId(recipientBudgetID, wallet.getId());
    }
    @Test(expected = BudgetDisabledException.class)
    public void testFundTransferToOtherBudget_ShouldThrowBudgetDisabledException_WhenSenderBudgetIsDisabled() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "some description";
        double amount = 69;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget senderBudget = Budget.builder()
                .isEnabled(false)
                .build();
        Budget recipientBudget = Budget.builder()
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(senderBudgetID, wallet.getId())).thenReturn(senderBudget);
        when(budgetRepository.findByIdAndWalletId(recipientBudgetID, wallet.getId())).thenReturn(recipientBudget);
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(senderBudgetID, wallet.getId());
        verify(budgetRepository).findByIdAndWalletId(recipientBudgetID, wallet.getId());
    }
    @Test(expected = BudgetDisabledException.class)
    public void testFundTransferToOtherBudget_ShouldThrowBudgetDisabledException_WhenRecipientBudgetIsDisabled() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "some description";
        double amount = 69;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget senderBudget = Budget.builder()
                .isEnabled(true)
                .build();
        Budget recipientBudget = Budget.builder()
                .isEnabled(false)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(senderBudgetID, wallet.getId())).thenReturn(senderBudget);
        when(budgetRepository.findByIdAndWalletId(recipientBudgetID, wallet.getId())).thenReturn(recipientBudget);
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(senderBudgetID, wallet.getId());
        verify(budgetRepository).findByIdAndWalletId(recipientBudgetID, wallet.getId());
    }
    @Test(expected = AccountingConstraintViolationException.class)
    public void testFundTransferToOtherBudget_ShouldThrowAccountingConstraintViolationException_WhenTheAmountToDebitFromTheSenderBudgetIsGreaterThanTheBalanceOfTheSenderBudget() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "some description";
        double amount = 69;
        double senderBudgetBalance = 68;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget senderBudget = Budget.builder()
                .isEnabled(true)
                .balance(senderBudgetBalance)
                .build();
        Budget recipientBudget = Budget.builder()
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(senderBudgetID, wallet.getId())).thenReturn(senderBudget);
        when(budgetRepository.findByIdAndWalletId(recipientBudgetID, wallet.getId())).thenReturn(recipientBudget);
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(senderBudgetID, wallet.getId());
        verify(budgetRepository).findByIdAndWalletId(recipientBudgetID, wallet.getId());
    }
    @Test
    public void testFundTransferToOtherBudget_ShouldThrowSucceed() throws NoSuchEntityOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        long senderBudgetID = 1L;
        long recipientBudgetID = 2L;
        String description = "some description";
        double amount = 69D;
        double senderBudgetBalance = 6969D;
        double recipientBudgetBalance = 6969D;
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget senderBudget = Budget.builder()
                .isEnabled(true)
                .balance(senderBudgetBalance)
                .build();
        Budget recipientBudget = Budget.builder()
                .isEnabled(true)
                .balance(recipientBudgetBalance)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(senderBudgetID, wallet.getId())).thenReturn(senderBudget);
        when(budgetRepository.findByIdAndWalletId(recipientBudgetID, wallet.getId())).thenReturn(recipientBudget);
        budgetService.fundTransferToOtherBudget(senderBudgetID, recipientBudgetID, description, amount);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(senderBudgetID, wallet.getId());
        verify(budgetRepository).findByIdAndWalletId(recipientBudgetID, wallet.getId());
        BudgetTransferHistoryDTO budgetTransferHistoryDTO = BudgetTransferHistoryDTO.builder()
                .senderBudget(senderBudget)
                .recipientBudget(recipientBudget)
                .amount(amount)
                .description(description)
                .wallet(wallet)
                .build();
        assertEquals(senderBudget.getBalance(), senderBudgetBalance - amount, 0.001);
        assertEquals(recipientBudget.getBalance(), recipientBudgetBalance + amount, 0.001);
        verify(budgetRepository).save(senderBudget);
        verify(budgetRepository).save(recipientBudget);
        verify(budgetTransferHistoryService).save(budgetTransferHistoryDTO);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testDisableFund_ShouldThrowIllegalArgumentException_WhenPasswordIsEmpty() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyDisabledException {
        long budgetID = 1L;
        String password = "";
        budgetService.disableFund(budgetID, password);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testDisableFund_ShouldThrowIllegalArgumentException_WhenPasswordIsNull() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyDisabledException {
        long budgetID = 1L;
        String password = null;
        budgetService.disableFund(budgetID, password);
    }

    @Test(expected = NoSuchEntityOrNotAuthorized.class)
    public void testDisableFund_ShouldThrowNoSuchEntityOrUnauthorized_WhenBudgetIDDoesntExistOrNotOwnedByUser() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyDisabledException {
        long budgetID = 1L;
        String password = "password";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(null);
        budgetService.disableFund(budgetID, password);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = BudgetAlreadyDisabledException.class)
    public void testDisableFund_ShouldThrowBudgetAlreadyDisabled_WhenBudgetIsAlreadyDisabled() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyDisabledException {
        long budgetID = 1L;
        String password = "password";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .isEnabled(false)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        budgetService.disableFund(budgetID, password);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = IncorrectPasswordException.class)
    public void testDisableFund_ShouldThrowIncorrectPasswordException_WhenTheProvidedPasswordDoesntMatchTheUsersAccountPassword() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyDisabledException {
        long budgetID = 1L;
        String password = "password";
        String hashedPassword = "hashedPassword";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .password(hashedPassword)
                .build();
        Budget budget = Budget.builder()
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(passwordEncoder.matches(password, loggedInUser.getPassword())).thenReturn(false);
        budgetService.disableFund(budgetID, password);
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(budgetRepository, times(2)).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test
    public void testDisableFund_ShouldSucceed() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyDisabledException {
        long budgetID = 1L;
        String password = "password";
        String hashedPassword = "hashedPassword";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .password(hashedPassword)
                .build();
        Budget budget = Budget.builder()
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(passwordEncoder.matches(password, loggedInUser.getPassword())).thenReturn(true);
        budgetService.disableFund(budgetID, password);
        assertFalse(budget.isEnabled());
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(budgetRepository, times(1)).findByIdAndWalletId(budgetID, wallet.getId());
        verify(passwordEncoder).matches(password, loggedInUser.getPassword());
        verify(budgetRepository).save(budget);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnableFund_ShouldThrowIllegalArgumentException_WhenPasswordIsEmpty() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyEnabledException {
        long budgetID = 1L;
        String password = "";
        budgetService.enableFund(budgetID, password);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testEnableFund_ShouldThrowIllegalArgumentException_WhenPasswordIsNull() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyEnabledException {
        long budgetID = 1L;
        String password = null;
        budgetService.enableFund(budgetID, password);
    }

    @Test(expected = NoSuchEntityOrNotAuthorized.class)
    public void testEnableFund_ShouldThrowNoSuchEntityOrUnauthorized_WhenBudgetIDDoesntExistOrNotOwnedByUser() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyEnabledException {
        long budgetID = 1L;
        String password = "password";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(null);
        budgetService.enableFund(budgetID, password);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = BudgetAlreadyEnabledException.class)
    public void testEnableFund_ShouldThrowBudgetAlreadyEnabled_WhenBudgetIsAlreadyDisabled() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyEnabledException {
        long budgetID = 1L;
        String password = "password";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .build();
        Budget budget = Budget.builder()
                .isEnabled(true)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        budgetService.enableFund(budgetID, password);
        verify(securityUtil).getLoggedInUser();
        verify(budgetRepository).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test(expected = IncorrectPasswordException.class)
    public void testEnableFund_ShouldThrowIncorrectPasswordException_WhenTheProvidedPasswordDoesntMatchTheUsersAccountPassword() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyEnabledException {
        long budgetID = 1L;
        String password = "password";
        String hashedPassword = "hashedPassword";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .password(hashedPassword)
                .build();
        Budget budget = Budget.builder()
                .isEnabled(false)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(passwordEncoder.matches(password, loggedInUser.getPassword())).thenReturn(false);
        budgetService.enableFund(budgetID, password);
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(budgetRepository, times(2)).findByIdAndWalletId(budgetID, wallet.getId());
    }
    @Test
    public void testEnableFund_ShouldSucceed() throws IncorrectPasswordException, NoSuchEntityOrNotAuthorized, BudgetAlreadyEnabledException {
        long budgetID = 1L;
        String password = "password";
        String hashedPassword = "hashedPassword";
        Wallet wallet = Wallet.builder()
                .id(1L)
                .build();
        UserEntity loggedInUser = UserEntity.builder()
                .wallet(wallet)
                .password(hashedPassword)
                .build();
        Budget budget = Budget.builder()
                .isEnabled(false)
                .build();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(budgetRepository.findByIdAndWalletId(budgetID, wallet.getId())).thenReturn(budget);
        when(passwordEncoder.matches(password, loggedInUser.getPassword())).thenReturn(true);
        budgetService.enableFund(budgetID, password);
        assertTrue(budget.isEnabled());
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(budgetRepository, times(1)).findByIdAndWalletId(budgetID, wallet.getId());
        verify(passwordEncoder).matches(password, loggedInUser.getPassword());
        verify(budgetRepository).save(budget);
    }
}
