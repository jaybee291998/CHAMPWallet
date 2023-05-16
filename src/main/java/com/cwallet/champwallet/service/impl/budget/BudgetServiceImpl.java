package com.cwallet.champwallet.service.impl.budget;

import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryDTO;
import com.cwallet.champwallet.exception.*;
import com.cwallet.champwallet.exception.budget.BudgetExpiredException;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.budget.BudgetAllocationHistory;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.budget.BudgetAllocationHistoryRepository;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.budget.BudgetTransferHistoryRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetService;
import com.cwallet.champwallet.service.budget.BudgetTransferHistoryService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private BudgetTransferHistoryService budgetTransferHistoryService;
    @Autowired
    private PasswordEncoder passwordEncoder;
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
    public void update(BudgetDTO budgetDTO, long budgetID) throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException {
        if(budgetDTO == null) {
            throw new IllegalArgumentException("budget dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, loggedInUser.getWallet().getId());
        if(budget == null) {
            throw new NoSuchBudgetOrNotAuthorized("No such budget or unauthorized");
        }
        if(!isUpdateable(mapToBudgetDTO(budget))) {
            throw new BudgetExpiredException("Budget is no longer updateable");
        }
        budget.setName(budgetDTO.getName());
        budget.setDescription(budgetDTO.getDescription());
        budgetRepository.save(budget);
    }

    @Override
    public boolean isUpdateable(BudgetDTO budgetDTO){
        if(expirableAndOwnedService.isExpired(budgetDTO)) {
            return false;
        } else if(!budgetDTO.isEnabled()) {
            return false;
        } else {
            return expenseRepository.findByBudgetId(budgetDTO.getId()).isEmpty();
        }
    }
    @Transactional
    @Override
    public void deleteBudget(long budgetID) throws NoSuchBudgetOrNotAuthorized, BudgetExpiredException, AccountingConstraintViolationException {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Wallet wallet = loggedInUser.getWallet();
        Budget budget = budgetRepository.findByIdAndWalletId(budgetID, wallet.getId());
        if(budget == null) {
            throw new NoSuchBudgetOrNotAuthorized("No such budget or unauthorized");
        }
        BudgetDTO budgetDTO = mapToBudgetDTO(budget);
        if(!isUpdateable(budgetDTO)) {
            throw new BudgetExpiredException("Budget no longer updateable");
        } else {
            // not expired yet, but check whether if this budget is in involved in any transfer transaction
            if(!budget.getOutgoingTransferHistory().isEmpty() || !budget.getIncomingTransferHistory().isEmpty()) {
                // the budget is involved in some kind of transaction
                throw new AccountingConstraintViolationException(String.format("This budget %s is involved on a transfer transaction, so it can no longer be deleted", budget.getName()));
            }
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
    public void allocateToBudget(@NonNull long budgetID, @NonNull double amount, @NonNull String description, @NonNull boolean isAllocate) throws NoSuchEntityOrNotAuthorized, AccountingConstraintViolationException, BudgetDisabledException {
        if(amount <= 0) {
            throw new IllegalArgumentException("amount cant be a negative number");
        }
        Budget budget = getBudget(budgetID);
        if(!budget.isEnabled()) {
            throw new BudgetDisabledException("This budget is inactive");
        }
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

    @Transactional
    @Override
    public void fundTransferToOtherBudget(long senderBudgetID, long recipientBudgetID, String description, double amount) throws NoSuchEntityOrNotAuthorized, AccountingConstraintViolationException, BudgetDisabledException {
        if(description == null || description.equals("")) {
            throw new IllegalArgumentException("description cannot be empty or null");
        }
        if(amount < 0) {
            throw new IllegalArgumentException("amount shouldnt be a negative number");
        }
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        Budget senderBudget = budgetRepository.findByIdAndWalletId(senderBudgetID, wallet.getId());
        Budget recipientBudget = budgetRepository.findByIdAndWalletId(recipientBudgetID, wallet.getId());
        if(!senderBudget.isEnabled()) {
            throw new BudgetDisabledException("This sender budget is inactive");
        }
        if(!recipientBudget.isEnabled()) {
            throw new BudgetDisabledException("This recipient budget is inactive");
        }
        if(senderBudget == null || recipientBudget == null) {
            throw new NoSuchEntityOrNotAuthorized("sending budget or receiving budget does not exist or not authorized");
        }
        if(senderBudget.getId() == recipientBudget.getId()) {
            throw new IllegalArgumentException("Transfer to itself is not allowed");
        }
        if(amount > senderBudget.getBalance()) {
            throw new AccountingConstraintViolationException(String.format("The amount(%.2f) to be transferred is greater than the balance of the budget(%.2f)", amount, senderBudget.getBalance()));
        }
        // initiate the transfer
        senderBudget.setBalance(senderBudget.getBalance() - amount);
        recipientBudget.setBalance(recipientBudget.getBalance() + amount);
        // build the transfer history
        BudgetTransferHistoryDTO budgetTransferHistoryDTO = BudgetTransferHistoryDTO.builder()
                .senderBudget(senderBudget)
                .recipientBudget(recipientBudget)
                .amount(amount)
                .description(description)
                .wallet(wallet)
                .build();
        // save the changes to the database
        budgetRepository.save(senderBudget);
        budgetRepository.save(recipientBudget);
        // save the history
        budgetTransferHistoryService.save(budgetTransferHistoryDTO);
    }

    @Override
    public void disableFund(long budgetID, String password) throws NoSuchEntityOrNotAuthorized, BudgetAlreadyDisabledException, IncorrectPasswordException {
        if(password == null || password.equals("")) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }
        Budget budgetToDisable = getBudget(budgetID);
        if(!budgetToDisable.isEnabled()) {
            throw new BudgetAlreadyDisabledException("This budget is already disabled");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        String hashedUserPassword = loggedInUser.getPassword();
        if(!passwordEncoder.matches(password, hashedUserPassword)) {
            throw new IncorrectPasswordException("The password you provided does not much your account password");
        }
        budgetToDisable.setEnabled(false);
        budgetRepository.save(budgetToDisable);
    }
    @Override
    public void enableFund(long budgetID, String password) throws NoSuchEntityOrNotAuthorized, BudgetAlreadyEnabledException, IncorrectPasswordException {
        if(password == null || password.equals("")) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }
        Budget budgetToEnable = getBudget(budgetID);
        if(budgetToEnable.isEnabled()) {
            throw new BudgetAlreadyEnabledException("This budget is already enabled");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        String hashedUserPassword = loggedInUser.getPassword();
        if(!passwordEncoder.matches(password, hashedUserPassword)) {
            throw new IncorrectPasswordException("The password you provided does not much your account password");
        }
        budgetToEnable.setEnabled(true);
        budgetRepository.save(budgetToEnable);
    }
}
