package com.cwallet.champwallet.service.impl;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
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

import static com.cwallet.champwallet.mappers.budget.BudgetMapper.mapToBudget;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

}
