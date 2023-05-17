package com.cwallet.champwallet.service.impl.income;

import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.income.Income;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.income.IncomeRepository;
import com.cwallet.champwallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.income.IncomeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static com.cwallet.champwallet.mappers.income.IncomeMapper.mapToIncome;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncomeServiceImplTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private IncomeTypeRepository incomeTypeRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ExpirableAndOwnedService expirableAndOwnedService;

    @InjectMocks
    private IncomeServiceImpl incomeServiceImpl;

    @Mock
    private IncomeService incomeService;

    @Mock
    private Income income;

    @Mock
    private UserEntity userEntity;

    @Mock
    private Wallet wallet;

    @Mock
    private NoSuchIncomeOrNotAuthorized noSuchIncomeOrNotAuthorized;

    @Mock
    private IncomeExpiredException incomeExpiredException;

    @Mock
    private AccountingConstraintViolationException accountingConstraintViolationException;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        incomeService = incomeServiceImpl;
    }
    @Test
    public void testSaveIncome() {
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setId(1L);
        incomeDTO.setSource("Save Source");
        incomeDTO.setDescription("Save Description");
        incomeDTO.setAmount(100);
        incomeDTO.setCreationTime(LocalDateTime.now());
        incomeDTO.setWallet(new Wallet());

        IncomeType incomeType = new IncomeType();
        incomeType.setId(1L);
        incomeType.setName("Save Name");
        incomeType.setDescription("Save Description");
        incomeType.setWallet(new Wallet());
        incomeType.setCreationTime(LocalDateTime.now());

        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        userEntity.setWallet(wallet);

        when(incomeTypeRepository.findById(incomeType.getId())).thenReturn(Optional.of(incomeType));
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(incomeRepository.save(any(Income.class))).thenReturn(new Income());
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        boolean result = incomeServiceImpl.save(incomeDTO, incomeType);
        assertTrue(result);

        verify(incomeTypeRepository, times(1)).findById(incomeType.getId());
        verify(securityUtil).getLoggedInUser();
        verify(incomeRepository).save(any(Income.class));
        verify(walletRepository).save(any(Wallet.class));

    }

    @Test
    public void getAllUserIncomeTest(){
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        List<Income> usersIncome = new ArrayList<>();
        Income income1 = new Income();
        income1.setId(1L);
        income1.setDescription("Description");
        income1.setAmount(100.0);
        income1.setTimestamp(LocalDateTime.now());
        usersIncome.add(income1);
        Income income2 = new Income();
        income2.setId(2L);
        income2.setDescription("Description2");
        income2.setAmount(200.0);
        income2.setTimestamp(LocalDateTime.now());
        usersIncome.add(income2);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(incomeRepository.findByWalletId(wallet.getId())).thenReturn(usersIncome);

        List<IncomeDTO> result = incomeServiceImpl.getAllUserIncome();

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByWalletId(wallet.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Description", result.get(0).getDescription());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Description2", result.get(1).getDescription());
    }
    @Test
    public void getSpecificIncomeTest() throws NoSuchIncomeOrNotAuthorized {

        long incomeId = 1L;

        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setId(1L);
        incomeDTO.setSource("Source");
        incomeDTO.setIncomeType(new IncomeType());
        incomeDTO.setDescription("Description");
        incomeDTO.setAmount(100.0);
        incomeDTO.setCreationTime(LocalDateTime.now());
        incomeDTO.setWallet(new Wallet());

        Income income = new Income();
        income.setId(1L);
        income.setDescription("Test Income");
        income.setAmount(100.0);
        income.setTimestamp(LocalDateTime.now());
        income.setIncomeType(new IncomeType());

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(income);

        IncomeDTO result = incomeServiceImpl.getSpecificIncome(incomeId);

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());

        assertEquals(1L, result.getId());
        assertEquals("Test Income", result.getDescription());
        assertEquals(100.0, result.getAmount());
    }

    @Test
    public void getSpecificIncomeTest_InvalidIncome(){

        long incomeId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        userEntity.setWallet(wallet);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(null);

        assertThrows(NoSuchIncomeOrNotAuthorized.class, () -> incomeServiceImpl.getSpecificIncome(incomeId
        ));

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());
    }

    @Test
    public void updateTest(){
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000.0);
        userEntity.setWallet(wallet);
        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        Income existingIncome = new Income();
        existingIncome.setId(1L);
        existingIncome.setAmount(500.0);
        when(incomeRepository.findByIdAndWalletId(1L, wallet.getId())).thenReturn(existingIncome);

        IncomeDTO updatedIncomeDTO = new IncomeDTO();
        updatedIncomeDTO.setSource("Updated Source");
        updatedIncomeDTO.setDescription("Updated Description");
        updatedIncomeDTO.setAmount(200.0);
        updatedIncomeDTO.setIncomeType(new IncomeType());

        assertDoesNotThrow(() -> incomeServiceImpl.update(updatedIncomeDTO, 1L));

        verify(securityUtil, times(2)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(1L, wallet.getId());
        verify(incomeRepository, times(1)).save(existingIncome);

        assertEquals("Updated Source", existingIncome.getSourceOfIncome());
        assertEquals("Updated Description", existingIncome.getDescription());
        assertEquals(200.0, existingIncome.getAmount());
        assertEquals(700.0, wallet.getBalance());

    }

    @Test
    public void update_NullIncomeDTO() {
        assertThrows(IllegalArgumentException.class, () -> incomeServiceImpl.update(null, 1L));

        verifyNoInteractions(securityUtil);
        verifyNoInteractions(incomeRepository);
    }
    @Test
    public void update_InvalidIncome() {

        long incomeId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000.0);
        userEntity.setWallet(wallet);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(null);
        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setAmount(500.0);

        assertThrows(NoSuchIncomeOrNotAuthorized.class, () -> incomeService.update(incomeDTO, incomeId));

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());
        verify(incomeRepository, never()).save(any(Income.class));
        assertEquals(1000.0, wallet.getBalance());
    }

    @Test
    public void update_NotUpdatable() {

        long incomeId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000.0);
        userEntity.setWallet(wallet);

        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setId(1L);
        incomeDTO.setSource("DTO Source");
        incomeDTO.setIncomeType(new IncomeType());
        incomeDTO.setDescription("DTO Description");
        incomeDTO.setAmount(1000.0);
        incomeDTO.setCreationTime(LocalDateTime.now());

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);

        Income existingIncome = new Income();
        existingIncome.setId(incomeId);
        existingIncome.setAmount(500.0);

        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(existingIncome);
        when(incomeServiceImpl.isUpdateable(eq(incomeDTO))).thenReturn(true);

        assertThrows(IncomeExpiredException.class, () -> incomeService.update(incomeDTO, incomeId));

        verify(securityUtil, times(1)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());
        verify(incomeRepository, never()).save(any(Income.class));
        assertEquals(1000.0, wallet.getBalance());
    }


    @Test
    public void update_ConstraintViolation() throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException, AccountingConstraintViolationException {

        long incomeId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(1000.0);
        userEntity.setWallet(wallet);

        IncomeDTO incomeDTO = new IncomeDTO();
        incomeDTO.setId(1L);
        incomeDTO.setSource("DTO Source");
        incomeDTO.setIncomeType(new IncomeType());
        incomeDTO.setDescription("DTO Description");
        incomeDTO.setAmount(1500.0);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        Income existingIncome = new Income();
        existingIncome.setId(incomeId);
        existingIncome.setAmount(500.0);
        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(existingIncome);
//        when(incomeServiceImpl.isUpdateable(eq(incomeDTO))).thenReturn(true);
        when(expirableAndOwnedService.isExpired(incomeDTO)).thenReturn(false);

        assertThrows(AccountingConstraintViolationException.class, () -> incomeService.update(incomeDTO, incomeId));

        verify(securityUtil, times(2)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());
//        verify(incomeRepository, never()).save(any(Income.class));
//        assertEquals(1000.0, wallet.getBalance());
    }

    @Test
    public void deleteIncomeTest() throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException {

        long incomeId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(500.0);
        userEntity.setWallet(wallet);

        Income income = new Income();
        income.setId(incomeId);
        income.setAmount(500.0);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(income);
        when(expirableAndOwnedService.isExpired(any())).thenReturn(false);

        incomeService.deleteIncome(incomeId);

        verify(expirableAndOwnedService, times(1)).isExpired(any());
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());
        verify(incomeRepository, times(1)).delete(income);
        assertEquals(0.0, wallet.getBalance());
    }

    @Test
    public void deleteIncome_InvalidIncome() {

        long incomeId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(500.0);
        userEntity.setWallet(wallet);

        Income income = new Income();
        income.setId(incomeId);
        income.setAmount(500.0);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(null);

        assertThrows(NoSuchIncomeOrNotAuthorized.class,()-> incomeService.deleteIncome(incomeId));

        verify(securityUtil, times(2)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());
        verify(incomeRepository, never()).delete(any(Income.class));
        assertEquals(500.0, wallet.getBalance());
    }

    @Test
    public void deleteIncome_NotUpdatable() throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException {

        long incomeId = 1L;
        UserEntity userEntity = new UserEntity();
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(500.0);
        userEntity.setWallet(wallet);

        Income income = new Income();
        income.setId(incomeId);
        income.setAmount(500.0);

        when(securityUtil.getLoggedInUser()).thenReturn(userEntity);
        when(incomeRepository.findByIdAndWalletId(incomeId, wallet.getId())).thenReturn(income);
        when(expirableAndOwnedService.isExpired(any())).thenReturn(true);

        assertThrows(IncomeExpiredException.class,()-> incomeService.deleteIncome(incomeId));

        verify(expirableAndOwnedService, times(1)).isExpired(any());
        verify(securityUtil, times(2)).getLoggedInUser();
        verify(incomeRepository, times(1)).findByIdAndWalletId(incomeId, wallet.getId());
        verify(incomeRepository, never()).delete(any(Income.class));
        assertEquals(500.0, wallet.getBalance());
    }

}