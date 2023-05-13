package com.cwallet.champwallet.service.expenseType.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.cwallet.champwallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.champwallet.exception.expenseType.ExpenseTypeExpiredException;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.expenseType.ExpenseTypeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import junit.framework.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class ExpenseTypeServiceImplTest {
    private ExpenseTypeService expenseTypeService;
    @Mock
    private ExpenseTypeDto expenseTypeDto;
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseTypeRepository expenseTypeRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private ExpirableAndOwnedService expirableAndOwnedService;

    @InjectMocks
    private ExpenseTypeServiceImpl expenseTypeServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        expenseTypeService = expenseTypeServiceImpl;
    }

    @Test
    void testSave() {
        // Prepare test data
        ExpenseTypeDto expenseTypeDto = new ExpenseTypeDto();
        expenseTypeDto.setId(1L);
        expenseTypeDto.setName("Save Name");
        expenseTypeDto.setDescription("Save Description");
        expenseTypeDto.setEnabled(true);
        expenseTypeDto.setCreationTime(LocalDateTime.now());
        expenseTypeDto.setWallet(new Wallet());


        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the expenseTypeRepository.save() method
        when(expenseTypeRepository.save(any(ExpenseType.class))).thenAnswer(invocation -> {
            ExpenseType savedExpenseType = invocation.getArgument(0);
            assertNotNull(savedExpenseType.getWallet());
            assertEquals(wallet, savedExpenseType.getWallet());
            return savedExpenseType;
        });

        // Perform the save operation and check for exceptions
        assertDoesNotThrow(() -> {
            boolean result = expenseTypeService.save(expenseTypeDto);
            // Verify the return value
            assertTrue(result);
        });

        // Verify the interactions
        verify(expenseTypeRepository, times(1)).save(any(ExpenseType.class));
    }

    @Test
    void testSave_Exception() {
        // Prepare test data
        ExpenseTypeDto expenseTypeDto = new ExpenseTypeDto();

        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the expenseTypeRepository.save() method to throw an exception
        when(expenseTypeRepository.save(any(ExpenseType.class))).thenThrow(new RuntimeException("Save failed"));

        // Perform the save operation
        boolean result = expenseTypeService.save(expenseTypeDto);

        // Verify the interactions
        verify(expenseTypeRepository, times(1)).save(any(ExpenseType.class));

        // Verify the return value
        assertFalse(result);
    }

    @Test
    void testGetAllUserExpenseType() {
        // Prepare test data
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the expenseTypeRepository.findByWalletId() method
        List<ExpenseType> expenseTypes = new ArrayList<>();
        ExpenseType expenseType1 = new ExpenseType();
        expenseType1.setId(1L);
        expenseType1.setName("Expense Type 1");
        expenseTypes.add(expenseType1);
        ExpenseType expenseType2 = new ExpenseType();
        expenseType2.setId(2L);
        expenseType2.setName("Expense Type 2");
        expenseTypes.add(expenseType2);
        when(expenseTypeRepository.findByWalletId(wallet.getId())).thenReturn(expenseTypes);

        // Perform the getAllUserExpenseType operation
        List<ExpenseTypeDto> result = expenseTypeService.getAllUserExpenseType();

        // Verify the interactions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseTypeRepository, times(1)).findByWalletId(wallet.getId());

        // Verify the return value
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Expense Type 1", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Expense Type 2", result.get(1).getName());
    }

    @Test
    void testGetExpenseTypeId() throws NoSuchExpenseTypeOrNotAuthorized {
        // Prepare test data
        long id = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the expenseTypeRepository.findByIdAndWalletId() method
        ExpenseType expenseType = new ExpenseType();
        expenseType.setId(id);
        expenseType.setName("Expense Type");
        when(expenseTypeRepository.findByIdAndWalletId(id, wallet.getId())).thenReturn(expenseType);

        // Perform the getExpenseTypeId operation
        ExpenseTypeDto result = expenseTypeService.getExpenseTypeId(id);

        // Verify the interactions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseTypeRepository, times(1)).findByIdAndWalletId(id, wallet.getId());

        // Verify the return value
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Expense Type", result.getName());
    }

    @Test
    void testUpdateExpenseType() {
        // Prepare test data
        long expenseTypeId = 1L;
        ExpenseTypeDto expenseTypeDto = new ExpenseTypeDto();
        expenseTypeDto.setId(1L);
        expenseTypeDto.setName("Expense Type Name");
        expenseTypeDto.setDescription("Expense Type Description");
        expenseTypeDto.setEnabled(true);
        expenseTypeDto.setCreationTime(LocalDateTime.now());
        expenseTypeDto.setWallet(new Wallet());
        expenseTypeDto.setName("Updated Expense Type");
        expenseTypeDto.setDescription("Updated Expense Type Description");

        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the expenseTypeRepository.findByIdAndWalletId() method
        ExpenseType expenseType = new ExpenseType();
        expenseType.setId(expenseTypeId);
        expenseType.setName("Expense Type");
        expenseType.setDescription("Expense Type Description");
        when(expenseTypeRepository.findByIdAndWalletId(expenseTypeId, wallet.getId())).thenReturn(expenseType);

        // Perform the updateExpenseType operation
        assertDoesNotThrow(() -> expenseTypeService.updateExpenseType(expenseTypeDto, expenseTypeId));

        // Verify the interactions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseTypeRepository, times(1)).findByIdAndWalletId(expenseTypeId, wallet.getId());
        verify(expenseTypeRepository, times(1)).save(expenseType);

        // Verify the updated expense type
        assertEquals("Updated Expense Type", expenseType.getName());
        assertEquals("Updated Expense Type Description", expenseType.getDescription());
    }


    //<----------------------->
    @Test
    public void testIsUpdatable_WhenExpenseTypeDtoIsExpired_ShouldReturnFalse() {
        // Arrange
        ExpenseTypeDto expenseTypeDto = new ExpenseTypeDto();
        expenseTypeDto.setId(1L);
        when(expirableAndOwnedService.isExpired(expenseTypeDto)).thenReturn(true);
        when(expenseRepository.findByExpenseTypeId(expenseTypeDto.getId())).thenReturn(new ArrayList<Expense>());

        // Act
        boolean result = expenseTypeService.isUpdatable(expenseTypeDto);

        // Assert
        assertFalse(result);
        verify(expirableAndOwnedService, times(1)).isExpired(expenseTypeDto);
        verifyNoInteractions(expenseRepository);
    }


    @Test
    public void testIsUpdatable_WhenExpenseTypeDtoIsNotExpiredAndHasAssociatedExpenses_ShouldReturnFalse() {
        // Arrange
        ExpenseTypeDto expenseTypeDto = new ExpenseTypeDto();
        expenseTypeDto.setId(1L);
        when(expirableAndOwnedService.isExpired(expenseTypeDto)).thenReturn(false);
        when(expenseRepository.findByExpenseTypeId(expenseTypeDto.getId())).thenReturn(new ArrayList<Expense>());

        // Act
        boolean result = expenseTypeService.isUpdatable(expenseTypeDto);

        // Assert
        assert (result);
        verify(expirableAndOwnedService, times(1)).isExpired(expenseTypeDto);
        verify(expenseRepository, times(1)).findByExpenseTypeId(expenseTypeDto.getId());
    }

    @Test
    public void testIsUpdatable_WhenExpenseTypeDtoIsNotExpiredAndHasNoAssociatedExpenses_ShouldReturnTrue() {
        // Arrange
        ExpenseTypeDto expenseTypeDto = new ExpenseTypeDto();
        expenseTypeDto.setId(1L);
        when(expirableAndOwnedService.isExpired(expenseTypeDto)).thenReturn(false);
        when(expenseRepository.findByExpenseTypeId(expenseTypeDto.getId())).thenReturn(new ArrayList<Expense>());

        // Act
        boolean result = expenseTypeService.isUpdatable(expenseTypeDto);

        // Assert
        assertTrue(result);
        verify(expirableAndOwnedService, times(1)).isExpired(expenseTypeDto);
        verify(expenseRepository, times(1)).findByExpenseTypeId(expenseTypeDto.getId());
    }

    @Test
    public void testIsUpdatable_WhenExpenseTypeDtoIsNull_ShouldThrowIllegalArgumentException() {
        // Arrange
        ExpenseTypeDto expenseTypeDto1 = null;

        when(expirableAndOwnedService.isExpired(expenseTypeDto)).thenReturn(true);
        when(expenseRepository.findByExpenseTypeId(expenseTypeDto.getId())).thenReturn(new ArrayList<>());

        // Act and Assert
        when(expenseRepository.findByExpenseTypeId(expenseTypeDto.getId()).isEmpty()).thenThrow(IllegalArgumentException.class);

        verifyNoInteractions(expirableAndOwnedService);
        verifyNoInteractions(expenseRepository);
    }
    //<------>

    @Test
    public void deleteExpenseType_ValidExpenseType_DeletesExpenseType() throws NoSuchExpenseTypeOrNotAuthorized, ExpenseTypeExpiredException {
        // Arrange
        long expenseTypeId = 1L;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        loggedInUser.setWallet(wallet);
        ExpenseType expenseType = new ExpenseType();
        expenseType.setId(expenseTypeId);
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(expenseTypeRepository.findByIdAndWalletId(expenseTypeId, wallet.getId())).thenReturn(expenseType);

        // Act
        expenseTypeService.deleteExpenseType(expenseTypeId);

        // Assert
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseTypeRepository, times(1)).findByIdAndWalletId(expenseTypeId, wallet.getId());
        verify(expenseTypeRepository, times(1)).delete(expenseType);
    }

    @Test
    public void deleteExpenseType_InvalidExpenseType_ThrowsNoSuchExpenseTypeOrNotAuthorized() throws NoSuchExpenseTypeOrNotAuthorized, ExpenseTypeExpiredException {
        // Arrange
        long expenseTypeId = 1L;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        loggedInUser.setWallet(wallet);
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(expenseTypeRepository.findByIdAndWalletId(expenseTypeId, wallet.getId())).thenReturn(null);

        // Assert
        assertThrows(NoSuchExpenseTypeOrNotAuthorized.class, () -> {
            // Act
            expenseTypeService.deleteExpenseType(expenseTypeId);
        });

        // Verify
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(expenseTypeRepository, times(1)).findByIdAndWalletId(expenseTypeId, wallet.getId());
        verify(expenseTypeRepository, never()).delete(any(ExpenseType.class));
    }


}
