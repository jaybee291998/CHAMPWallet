package com.cwallet.champwallet.service.impl.incomeType;

import com.cwallet.champwallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IncomeTypeServiceImplTest {
    @Mock
    private IncomeTypeRepository incomeTypeRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ExpirableAndOwnedService expirableAndOwnedService;

    @InjectMocks
    private IncomeTypeServiceImpl incomeTypeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void save_ValidIncomeTypeDto_ReturnsTrue() {
        // Arrange
        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setWallet(new Wallet()); // Set the wallet object if required
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.save(any(IncomeType.class))).thenReturn(new IncomeType());

        // Act
        boolean result = incomeTypeService.save(incomeTypeDto);

        // Assert
        assertTrue(result);
        verify(incomeTypeRepository, times(1)).save(any(IncomeType.class));
    }

    @Test
    void save_InvalidIncomeTypeDto_ReturnsFalse() {
        // Arrange
        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setWallet(new Wallet()); // Set the wallet object if required
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.save(any(IncomeType.class))).thenThrow(new RuntimeException());

        // Act
        boolean result = incomeTypeService.save(incomeTypeDto);

        // Assert
        assertFalse(result);
        verify(incomeTypeRepository, times(1)).save(any(IncomeType.class));
    }

    @Test
    void getAllIncomeType_ReturnsListOfIncomeTypeDto() {
        // Arrange
        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setWallet(new Wallet()); // Set the wallet object if required
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.findByWalletId(anyLong())).thenReturn(new ArrayList<>());

        // Act
        List<IncomeTypeDto> result = incomeTypeService.getAllIncomeType();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(incomeTypeRepository, times(1)).findByWalletId(anyLong());
    }

    @Test
    void getIncomeTypeById_ValidId_ReturnsIncomeTypeDto() throws NoSuchEntityOrNotAuthorized {
        // Arrange
        long id = 1;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(123); // Set the wallet ID
        loggedInUser.setWallet(wallet);
        IncomeType incomeType = new IncomeType();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.findByIdAndWalletId(id, wallet.getId())).thenReturn(incomeType);

        // Act
        IncomeTypeDto result = incomeTypeService.getIncomeTypeById(id);

        // Assert
        assertNotNull(result);
        assertEquals(incomeType, mapToIncomeType(result));
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(id, wallet.getId());
    }

    @Test
    void getIncomeTypeById_InexistentId_ThrowsNoSuchEntityOrNotAuthorized() {
        // Arrange
        long id = 1;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(123); // Set the wallet ID
        loggedInUser.setWallet(wallet);
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.findByIdAndWalletId(id, wallet.getId())).thenReturn(null);

        // Act and Assert
        assertThrows(NoSuchEntityOrNotAuthorized.class, () -> incomeTypeService.getIncomeTypeById(id));
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(id, wallet.getId());
    }

    private IncomeType mapToIncomeType(IncomeTypeDto incomeTypeDto) {
        // Sample implementation to map IncomeTypeDto to IncomeType
        IncomeType incomeType = new IncomeType();
        incomeType.setId(incomeTypeDto.getId());
        incomeType.setName(incomeTypeDto.getName());
        incomeType.setDescription(incomeTypeDto.getDescription());
        incomeType.setCreationTime(incomeTypeDto.getCreationTime());
        return incomeType;
    }

    @Test
    void getIncomeType_ValidId_ReturnsIncomeType() throws NoSuchEntityOrNotAuthorized {
        // Arrange
        long id = 1;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(123); // Set the wallet ID
        loggedInUser.setWallet(wallet);
        IncomeType incomeType = new IncomeType();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.findByIdAndWalletId(id, wallet.getId())).thenReturn(incomeType);

        // Act
        IncomeType result = incomeTypeService.getIncomeType(id);

        // Assert
        assertNotNull(result);
        assertEquals(incomeType, result);
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(id, wallet.getId());
    }

    @Test
    void getIncomeType_InexistentId_ThrowsNoSuchEntityOrNotAuthorized() {
        // Arrange
        long id = 1;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(123); // Set the wallet ID
        loggedInUser.setWallet(wallet);
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.findByIdAndWalletId(id, wallet.getId())).thenReturn(null);

        // Act and Assert
        assertThrows(NoSuchEntityOrNotAuthorized.class, () -> incomeTypeService.getIncomeType(id));
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(id, wallet.getId());
    }

    @Test
    void update_ValidDtoAndId_UpdatesIncomeType() throws NoSuchEntityOrNotAuthorized {
        // Arrange
        long id = 1;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(123); // Set the wallet ID
        loggedInUser.setWallet(wallet);
        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        incomeTypeDto.setName("Updated Name");
        incomeTypeDto.setDescription("Updated Description");
        IncomeType incomeType = new IncomeType();
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.findByIdAndWalletId(id, wallet.getId())).thenReturn(incomeType);

        // Act
        incomeTypeService.update(incomeTypeDto, id);

        // Assert
        assertEquals(incomeTypeDto.getName(), incomeType.getName());
        assertEquals(incomeTypeDto.getDescription(), incomeType.getDescription());
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(id, wallet.getId());
        verify(incomeTypeRepository, times(1)).save(incomeType);
    }

    @Test
    void update_NullDto_ThrowsIllegalArgumentException() {
        // Arrange
        long id = 1;
        IncomeTypeDto incomeTypeDto = null;

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> incomeTypeService.update(incomeTypeDto, id));
        verify(securityUtil, never()).getLoggedInUser();
        verify(incomeTypeRepository, never()).findByIdAndWalletId(anyLong(), anyLong());
        verify(incomeTypeRepository, never()).save(any(IncomeType.class));
    }

    @Test
    void update_InexistentId_ThrowsNoSuchEntityOrNotAuthorized() {
        // Arrange
        long id = 1;
        UserEntity loggedInUser = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(123); // Set the wallet ID
        loggedInUser.setWallet(wallet);
        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        incomeTypeDto.setName("Updated Name");
        incomeTypeDto.setDescription("Updated Description");
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);
        when(incomeTypeRepository.findByIdAndWalletId(id, wallet.getId())).thenReturn(null);

        // Act and Assert
        assertThrows(NoSuchEntityOrNotAuthorized.class, () -> incomeTypeService.update(incomeTypeDto, id));
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(id, wallet.getId());


    }



}