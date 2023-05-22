package com.cwallet.champwallet.service.impl.expense;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.BudgetDisabledException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.exception.expense.ExpenseExpiredException;
import com.cwallet.champwallet.exception.expense.NoSuchExpenseOrNotAuthorized;
import com.cwallet.champwallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.mappers.expense.ExpenseMapper;
import com.cwallet.champwallet.mappers.incomeType.IncomeTypeMapper;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.models.income.Income;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetService;
import com.cwallet.champwallet.service.expense.ExpenseService;
import com.cwallet.champwallet.service.expenseType.ExpenseTypeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.budget.BudgetMapper.mapToBudgetDTO;
import static com.cwallet.champwallet.mappers.expense.ExpenseMapper.*;
import static com.cwallet.champwallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseType;
import static com.cwallet.champwallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseTypeDto;
import static com.cwallet.champwallet.mappers.income.IncomeMapper.mapToIncomeDTO;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private ExpenseTypeRepository expenseTypeRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    private BudgetService budgetService;
    @Autowired
    private ExpenseTypeService expenseTypeService;
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean save(ExpenseDTO expenseDTO, ExpenseType expenseType, Budget budget) throws NoSuchEntityOrNotAuthorized, NoSuchBudgetOrNotAuthorized, NoSuchExpenseTypeOrNotAuthorized, BudgetDisabledException, AccountingConstraintViolationException {
        if(expenseDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("price must not be less than or equal to zero");
        }
        if(expenseDTO == null || expenseType == null || budget == null) {
            throw new NoSuchEntityOrNotAuthorized("ExpenseDTO, ExpenseType or Budget doesnt exist");
        }

        Long expenseTypeID = expenseType.getId();
        Long budgetID = budget.getId();
        Budget actualBudget = budgetService.getBudget(budgetID);
        ExpenseType actualExpenseType = mapToExpenseType(expenseTypeService.getExpenseTypeId(expenseTypeID));
        if(!actualBudget.isEnabled()) {
            throw new BudgetDisabledException("This budget is disabled");
        }
        double totalBalance= actualBudget.getBalance() - expenseDTO.getPrice();
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        Expense expense = mapToExpense(expenseDTO);
        expense.setWallet(wallet);
        expense.setBudget(actualBudget);
        expense.setExpenseType(actualExpenseType);

        if(expense.getPrice()<= actualBudget.getBalance()){
            actualBudget.setBalance(totalBalance);
            expenseRepository.save(expense);
            budgetRepository.save(budget);
            return true;
        } else {
            throw new AccountingConstraintViolationException(String.format("The price(%.2f) to debited to the budget is higher than the budgets balance(%.2f)",expenseDTO.getPrice(), actualBudget.getBalance()));
        }
    }

    @Override
    public List<ExpenseDTO> getAllUserExpense(LocalDate specificDate) {
        if(specificDate == null) specificDate = LocalDate.now();
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        LocalDateTime startDate = specificDate.atStartOfDay();
        LocalDateTime endDate = startDate.plusHours(24);
        List<Expense> usersExpense = expenseRepository.getExpensesWithinDateRange(loggedInUser.getWallet().getId(), startDate, endDate);
        return usersExpense.stream().map((expense) -> mapToExpenseDTO(expense)).collect(Collectors.toList());
    }

    @Override
    public List<ExpenseDTO> getAllUserExpenseAll() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<Expense>userExpense = expenseRepository.findByWalletId(loggedInUser.getWallet().getId());
        return userExpense.stream().map((expense) -> mapToExpenseDTO(expense)).collect(Collectors.toList());
    }

    @Override
    public ExpenseDTO getSpecificExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Expense expense = expenseRepository.findByIdAndWalletId(expenseID,loggedInUser.getWallet().getId());
        if(expense == null) {
            throw new NoSuchExpenseOrNotAuthorized("Not authorized or doesnt exsit");
        }
        ExpenseDTO expenseDTO = mapToExpenseDTO(expense);
        return expenseDTO;
    }

    @Override
    public boolean isUpdateable(ExpenseDTO expenseDTO) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException {
        if (expirableAndOwnedService.isExpired(expenseDTO)) {
            return false;
        } else {
            return expenseDTO.getBudget().isEnabled();
        }
    }
    public List<ExpenseJson> getExpensesWithinInterval(int intervalInDays) {
        if(intervalInDays <= 0) {
            throw new IllegalArgumentException("interval must not be less than or equal to zero");
        }
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(intervalInDays);
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        return expenseRepository.getExpensesWithinDateRange(wallet.getId(), start, end).stream().map(e -> mapToExpenseJson(e)).collect(Collectors.toList());
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void update(ExpenseDTO expenseDTO, long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, AccountingConstraintViolationException, NoSuchEntityOrNotAuthorized, BudgetDisabledException, NoSuchExpenseTypeOrNotAuthorized {
        if(expenseDTO == null) {
            throw new IllegalArgumentException("budget dto must not be null");
        }
        if(expenseDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("price must not be less than or equal to 0");
        }
        ExpenseType expenseType = mapToExpenseType(expenseTypeService.getExpenseTypeId(expenseDTO.getExpenseType().getId()));
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Expense expense = expenseRepository.findByIdAndWalletId(expenseID, loggedInUser.getWallet().getId());
        if(expense == null) {
            throw new NoSuchExpenseOrNotAuthorized("No such income or unauthorized");
        }
        if(!isUpdateable(expenseDTO)){
            throw new ExpenseExpiredException("Expense no longer updateable");
        }
        Budget oldBudget = expense.getBudget();
        Budget newBudget = budgetService.getBudget(expenseDTO.getBudget().getId());
        if(!oldBudget.isEnabled() || !newBudget.isEnabled()) {
            throw new BudgetDisabledException("The budget is disabled");
        }

        double oldPrice = expense.getPrice();
        double newPrice = expenseDTO.getPrice();

        // budget is unchanged
        if(oldBudget.getId() == newBudget.getId()) {
            if(newPrice > oldPrice) {
                // price increase
                double priceIncrease = newPrice - oldPrice;
                if(priceIncrease > oldBudget.getBalance()) {
                    // budget cant handle price increase
                    throw new AccountingConstraintViolationException(String.format("The price increase(%.2f) from the original price %.2f to the new price %.2f cant be debited to budget with balance of %.2f", priceIncrease, oldPrice, newPrice, oldBudget.getBalance()));
                }
                // budget can handle price increase
                // subtract the price increase the price increase to the budgets
                oldBudget.setBalance(oldBudget.getBalance() - priceIncrease);
            } else {
                // price decrease
                double priceDecrease = oldPrice - newPrice;
                // credit back the price decrease
                oldBudget.setBalance(oldBudget.getBalance() + priceDecrease);
            }
            // save changes to the database
            budgetRepository.save(oldBudget);
        } else {
            // budget changed
            // whether the price is changed or not is irrelevant
            // assume that there different the old price is whats debited to the old budget
            // so it makes sense that the old price would be credited back to the old budget
            // then the new budget will then be debited to the new budget
            // this is also the case when they're the same
            //check if the new budget can handle the transaction
            if(newPrice <= newBudget.getBalance()) {
                // can handle
                // debit the new price to the new budget
                newBudget.setBalance(newBudget.getBalance() - newPrice);
                // credit back the old price to the old budget
                oldBudget.setBalance(oldBudget.getBalance() + oldPrice);
                // save the changes to the database
                budgetRepository.save(newBudget);
                budgetRepository.save(oldBudget);
            } else {
                // cant handle
                throw new AccountingConstraintViolationException(String.format("The price %.2f cant be debited to the new budget with balance of %.2f", newPrice, newBudget.getBalance()));
            }
        }
        expense.setDescription(expenseDTO.getDescription());
        expense.setExpenseType(expenseType);
        expense.setPrice(newPrice);
        expense.setBudget(newBudget);
        expenseRepository.save(expense);
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, NoSuchEntityOrNotAuthorized, BudgetDisabledException {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
//        long budgetID = mapToExpenseDTO(expenseRepository.findById(expenseID)).getBudget().getId();
        Expense expense = expenseRepository.findByIdAndWalletId(expenseID, loggedInUser.getWallet().getId());
        if(expense == null) {
            throw new NoSuchExpenseOrNotAuthorized("No such Expense or unauthorized");
        }
        Budget actualBudget = expense.getBudget();
        if(!actualBudget.isEnabled()) {
            throw new BudgetDisabledException("Budget used in this expense has been disabled");
        }
        ExpenseDTO expenseDTO = mapToExpenseDTO(expense);
        if(!isUpdateable(expenseDTO)){
            throw new ExpenseExpiredException("Expense no longer updateable");
        }
        actualBudget.setBalance(actualBudget.getBalance() + expenseDTO.getPrice());
        expenseRepository.delete(expense);
    }

}
