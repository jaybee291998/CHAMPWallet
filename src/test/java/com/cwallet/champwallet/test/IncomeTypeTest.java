package com.cwallet.champwallet.test;

import com.cwallet.champwallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.champwallet.exception.EntityExpiredException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.incomeType.IncomeTypeService;
import com.cwallet.champwallet.service.impl.incomeType.IncomeTypeServiceImpl;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cwallet.champwallet.mappers.incomeType.IncomeTypeMapper.mapToIncomeTypeDto;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IncomeTypeTest {
    private IncomeTypeService incomeTypeService;

    @InjectMocks
    private IncomeTypeServiceImpl incomeTypeServiceImpl;

    @Mock
    private IncomeTypeRepository incomeTypeRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private ExpirableAndOwnedService expirableAndOwnedService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        incomeTypeService = incomeTypeServiceImpl;
    }

    @Test
    void testSave() {
        // Prepare test data
        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the incomeTypeRepository.save() method
        when(incomeTypeRepository.save(any(IncomeType.class))).thenAnswer(invocation -> {
            IncomeType savedIncomeType = invocation.getArgument(0);
            assertNotNull(savedIncomeType.getWallet());
            assertEquals(wallet, savedIncomeType.getWallet());
            return savedIncomeType;
        });

        // Perform the save operation
        assertDoesNotThrow(() -> incomeTypeService.save(incomeTypeDto));

        // Verify the interactions
        verify(incomeTypeRepository, times(1)).save(any(IncomeType.class));
    }
    @Test
    void testGetAllIncomeType() {
        // Prepare test data
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);
        List<IncomeType> incomeTypes = new ArrayList<>();
        // Add some sample income types to the list

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the incomeTypeRepository.findByWalletId() method
        when(incomeTypeRepository.findByWalletId(wallet.getId())).thenReturn(incomeTypes);

        // Perform the getAllIncomeType operation
        List<IncomeTypeDto> result = incomeTypeService.getAllIncomeType();

        // Verify the interactions and assertions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByWalletId(wallet.getId());
        assertEquals(incomeTypes.size(), result.size());
        // You can add additional assertions as per your requirements
    }
    @Test
    void testGetIncomeTypeById() throws NoSuchEntityOrNotAuthorized {
        // Prepare test data
        long incomeTypeId = 1;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);
        IncomeType incomeType = new IncomeType();
        // Set up the incomeType object with the required data

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the incomeTypeRepository.findByIdAndWalletId() method
        when(incomeTypeRepository.findByIdAndWalletId(incomeTypeId, wallet.getId())).thenReturn(incomeType);

        // Perform the getIncomeTypeById operation
        IncomeTypeDto expectedResult = mapToIncomeTypeDto(incomeType); // Convert to IncomeTypeDto
        IncomeTypeDto actualResult = incomeTypeService.getIncomeTypeById(incomeTypeId);

        // Verify the interactions and assertions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(incomeTypeId, wallet.getId());
        assertEquals(expectedResult, actualResult);
        // You can add additional assertions as per your requirements
    }

    @Test
    void testGetIncomeTypeByIdNotFound() {
        // Prepare test data
        long nonExistentIncomeTypeId = 1;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the incomeTypeRepository.findByIdAndWalletId() method to return null
        when(incomeTypeRepository.findByIdAndWalletId(nonExistentIncomeTypeId, wallet.getId())).thenReturn(null);

        // Perform the getIncomeTypeById operation and assert that it throws NoSuchEntityOrNotAuthorized
        assertThrows(NoSuchEntityOrNotAuthorized.class, () -> incomeTypeService.getIncomeTypeById(nonExistentIncomeTypeId));

        // Verify the interactions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(nonExistentIncomeTypeId, wallet.getId());
    }
    @Test
    void testGetIncomeType() throws NoSuchEntityOrNotAuthorized {
        // Prepare test data
        long incomeTypeId = 1;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);
        IncomeType incomeType = new IncomeType();
        // Set up the incomeType object with the required data

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the incomeTypeRepository.findByIdAndWalletId() method
        when(incomeTypeRepository.findByIdAndWalletId(incomeTypeId, wallet.getId())).thenReturn(incomeType);

        // Perform the getIncomeType operation
        IncomeType result = incomeTypeService.getIncomeType(incomeTypeId);

        // Verify the interactions and assertions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(incomeTypeId, wallet.getId());
        assertEquals(incomeType, result);
        // You can add additional assertions as per your requirements
    }
    @Test
    void testUpdateIncomeType() throws NoSuchEntityOrNotAuthorized {
        // Prepare test data
        long incomeTypeId = 1;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);
        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        incomeTypeDto.setName("New Name");
        incomeTypeDto.setDescription("New Description");
        IncomeType incomeType = new IncomeType();
        // Set up the incomeType object with the required data

        // Mock the securityUtil.getLoggedInUser() method
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        // Mock the incomeTypeRepository.findByIdAndWalletId() method
        when(incomeTypeRepository.findByIdAndWalletId(incomeTypeId, wallet.getId())).thenReturn(incomeType);

        // Perform the update operation
        incomeTypeService.update(incomeTypeDto, incomeTypeId);

        // Verify the interactions and assertions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(incomeTypeId, wallet.getId());
        verify(incomeTypeRepository, times(1)).save(incomeType);
        assertEquals(incomeTypeDto.getName(), incomeType.getName());
        assertEquals(incomeTypeDto.getDescription(), incomeType.getDescription());
        // You can add additional assertions as per your requirements
    }
    @Test
    void testIsUpdateable() {
        // Prepare test data
        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        incomeTypeDto.setId(1);

        // Mock the expirableAndOwnedService.isExpired() method
        when(expirableAndOwnedService.isExpired(incomeTypeDto)).thenReturn(false);

        // Mock the incomeTypeRepository.findByWalletId() method
        when(incomeTypeRepository.findByWalletId(incomeTypeDto.getId())).thenReturn(Collections.emptyList());

        // Perform the isUpdateable operation
        boolean isUpdateable = incomeTypeService.isUpdateable(incomeTypeDto);

        // Verify the interactions and assertions
        verify(expirableAndOwnedService, times(1)).isExpired(incomeTypeDto);
        verify(incomeTypeRepository, times(1)).findByWalletId(incomeTypeDto.getId());
        assertTrue(isUpdateable);
        // You can add additional assertions as per your requirements
    }
    @Test
    void testDeleteIncomeType() throws NoSuchEntityOrNotAuthorized, EntityExpiredException {
        // Prepare test data
        long incomeTypeId = 1;
        long walletId = 1;

        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setWallet(new Wallet());
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);

        IncomeType incomeType = new IncomeType();
        when(incomeTypeRepository.findByIdAndWalletId(incomeTypeId, walletId)).thenReturn(incomeType);

        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        when(incomeTypeService.isUpdateable(incomeTypeDto)).thenReturn(true);

        // Perform the deleteIncomeType operation
        assertDoesNotThrow(() -> incomeTypeService.deleteIncomeType(incomeTypeId));

        // Verify the interactions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(incomeTypeId, walletId);
        verify(incomeTypeService, times(1)).isUpdateable(incomeTypeDto);
        verify(incomeTypeRepository, times(1)).delete(incomeType);
    }

    @Test
    void testDeleteIncomeType_ThrowsNoSuchEntityOrNotAuthorized() {
        // Prepare test data
        long incomeTypeId = 1;
        long walletId = 1;

        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setWallet(new Wallet());
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);

        when(incomeTypeRepository.findByIdAndWalletId(incomeTypeId, walletId)).thenReturn(null);

        // Perform the deleteIncomeType operation and verify the exception
        assertThrows(NoSuchEntityOrNotAuthorized.class, () -> incomeTypeService.deleteIncomeType(incomeTypeId));

        // Verify the interactions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(incomeTypeId, walletId);
        verify(incomeTypeService, never()).isUpdateable(any());
        verify(incomeTypeRepository, never()).delete(any());
    }

    @Test
    void testDeleteIncomeType_ThrowsEntityExpiredException() throws NoSuchEntityOrNotAuthorized {
        // Prepare test data
        long incomeTypeId = 1;
        long walletId = 1;

        UserEntity loggedInUser = new UserEntity();
        loggedInUser.setWallet(new Wallet());
        when(securityUtil.getLoggedInUser()).thenReturn(loggedInUser);

        IncomeType incomeType = new IncomeType();
        when(incomeTypeRepository.findByIdAndWalletId(incomeTypeId, walletId)).thenReturn(incomeType);

        IncomeTypeDto incomeTypeDto = new IncomeTypeDto();
        when(incomeTypeService.isUpdateable(incomeTypeDto)).thenReturn(false);

        // Perform the deleteIncomeType operation and verify the exception
        assertThrows(EntityExpiredException.class, () -> incomeTypeService.deleteIncomeType(incomeTypeId));

        // Verify the interactions
        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeTypeRepository, times(1)).findByIdAndWalletId(incomeTypeId, walletId);
        verify(incomeTypeService, times(1)).isUpdateable(incomeTypeDto);
        verify(incomeTypeRepository, never()).delete(any());
    }
}


