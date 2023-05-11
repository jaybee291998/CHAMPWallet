package com.cwallet.champwallet.service.impl.budget;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.BudgetExpiredException;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.budget.BudgetAllocationHistory;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.budget.BudgetAllocationHistoryRepository;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.budget.BudgetMapper.mapToBudget;
import static com.cwallet.champwallet.mappers.budget.BudgetMapper.mapToBudgetDTO;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private BudgetAllocationHistoryRepository budgetAllocationHistoryRepository;
    @Autowired
    private WalletRepository walletRepository;

    @Override
    public boolean save(BudgetDTO budgetDTO) {
        Budget budget = mapToBudget(budgetDTO);
        budget.setWallet(securityUtil.getLoggedInUser().getWallet());
        budget.setEnabled(true);
        budget.setBalance(0);
        try {
            budgetRepository.save(budget);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<BudgetDTO> getAllUserBudget() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<Budget> usersBudget = budgetRepository.findByWalletId(loggedInUser.getWallet().getId());
        return usersBudget.stream().map(budget -> mapToBudgetDTO(budget)).collect(Collectors.toList());
    }

    @Override
    public BudgetDTO getSpecificBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, loggedInUser.getWallet().getId());
        if(budget == null) {
            throw new NoSuchBudgetOrNotAuthorized("Not authorized or doesnt exsit");
        }
        BudgetDTO budgetDTO = mapToBudgetDTO(budget);
        return budgetDTO;
    }

    @Override
    public void update(BudgetDTO budgetDTO, long budgetID) throws NoSuchBudgetOrNotAuthorized {
        if(budgetDTO == null) {
            throw new IllegalArgumentException("budget dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, loggedInUser.getWallet().getId());
        if(budget == null) {
            throw new NoSuchBudgetOrNotAuthorized("No such budget or unauthorized");
        }
        budget.setName(budgetDTO.getName());
        budget.setDescription(budgetDTO.getDescription());
        budgetRepository.save(budget);
    }

    @Override
    public boolean isUpdateable(BudgetDTO budgetDTO){
        if(expirableAndOwnedService.isExpired(budgetDTO)) {
            return false;
        } else {
            return expenseRepository.findByBudgetId(budgetDTO.getId()).isEmpty();
        }
    }
    @Transactional
    @Override
    public void deleteBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Wallet wallet = loggedInUser.getWallet();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, wallet.getId());
        if(budget == null) {
            throw new NoSuchBudgetOrNotAuthorized("No such budget or unauthorized");
        }
        BudgetDTO budgetDTO = mapToBudgetDTO(budget);
        if(!isUpdateable(budgetDTO)){
            throw new BudgetExpiredException("Budget no longer updateable");
        }
        wallet.setBalance(wallet.getBalance() + budgetDTO.getBalance());
        walletRepository.save(wallet);
        budgetRepository.delete(budget);
    }

    public Budget getBudget(long budgetID) throws NoSuchEntityOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, loggedInUser.getWallet().getId());
        if(budget == null) {
            throw new NoSuchEntityOrNotAuthorized("Not authorized or doesnt exsit");
        }
        return budget;
    }
    @Transactional
    @Override
    public void allocateToBudget(@NonNull long budgetID, @NonNull double amount, @NonNull String description, @NonNull boolean isAllocate) throws NoSuchEntityOrNotAuthorized, AccountingConstraintViolationException {
        if(amount <= 0) {
            throw new IllegalArgumentException("amount cant be a negative number");
        }
        Budget budget = getBudget(budgetID);
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        if(isAllocate) {
            if(wallet.getBalance() < amount) {
                throw new AccountingConstraintViolationException(String.format("Wallet balance is only %.2f", wallet.getBalance()));
            }
            budget.setBalance(budget.getBalance() + amount);
            wallet.setBalance(wallet.getBalance() - amount);
        } else {
            if(budget.getBalance() < amount) {
                throw new AccountingConstraintViolationException(String.format("Budget balance is only %.2f", budget.getBalance()));
            }
            budget.setBalance(budget.getBalance() - amount);
            wallet.setBalance(wallet.getBalance() + amount);
        }
        BudgetAllocationHistory history = BudgetAllocationHistory.builder()
                .isAllocate(isAllocate)
                .description(description)
                .wallet(wallet)
                .budget(budget)
                .amount(amount)
                .build();
        budgetAllocationHistoryRepository.save(history);
        walletRepository.save(wallet);
        budgetRepository.save(budget);
    }
}