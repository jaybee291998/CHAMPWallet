package com.cwallet.champwallet.service.impl.expense;

import com.cwallet.champwallet.dto.ExpenseJson;
import com.cwallet.champwallet.dto.expense.ExpenseDTO;
import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.exception.expense.ExpenseExpiredException;
import com.cwallet.champwallet.exception.expense.NoSuchExpenseOrNotAuthorized;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.models.expense.Expense;
import com.cwallet.champwallet.models.income.Income;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.budget.BudgetRepository;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetService;
import com.cwallet.champwallet.service.expense.ExpenseService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.expense.ExpenseMapper.*;
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
    @Override
    @Transactional
    public boolean save(ExpenseDTO expenseDTO, ExpenseType expenseType, Budget budget) {
        Long expenseTypeID = expenseType.getId();
        Long budgetID = budget.getId();
        Optional<Budget> optionalBudgets = budgetRepository.findById(budgetID);
        Budget actualBudget = optionalBudgets.get();
        Optional<ExpenseType> expenseTypes = expenseTypeRepository.findById(expenseTypeID);
        double totalBalance= actualBudget.getBalance() - expenseDTO.getPrice();
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        Expense expense = mapToExpense(expenseDTO);
        expense.setWallet(wallet);
        expense.setBudget(actualBudget);
//        incomeDTO.setWallet();
        expense.setExpenseType(expenseTypes.get());

        try {
            if(expense.getPrice()<= actualBudget.getBalance()){
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                actualBudget.setBalance(totalBalance);
                expenseRepository.save(expense);
                budgetRepository.save(budget);
                System.out.println("saved successfully");
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//                budgets(budget.setBalance(totalBalance));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ExpenseDTO> getAllUserExpense() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<Expense> usersExpense = expenseRepository.findByWalletId(loggedInUser.getWallet().getId());
        return usersExpense.stream().map((expense) -> mapToExpenseDTO(expense)).collect(Collectors.toList());
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
        }
        return true;
    }
    public List<ExpenseJson> getExpensesWithinInterval(int intervalInDays) {
        if(intervalInDays <= 0) {
            throw new IllegalArgumentException("interval must not be less than or equal to zero");
        }
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(intervalInDays);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(intervalInDays);
        System.out.println(start);
        System.out.println(end);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        return expenseRepository.getExpensesWithinDateRange(wallet.getId(), start, end).stream().map(e -> mapToExpenseJson(e)).collect(Collectors.toList());
    }


    @Override
    public void update(ExpenseDTO expenseDTO, long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, AccountingConstraintViolationException, NoSuchEntityOrNotAuthorized {
        if(expenseDTO == null) {
            throw new IllegalArgumentException("budget dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Expense expense = expenseRepository.findByIdAndWalletId(expenseID, loggedInUser.getWallet().getId());
        if(expense == null) {
            throw new NoSuchExpenseOrNotAuthorized("No such income or unauthorized");
        }
        if(!isUpdateable(expenseDTO)){
            throw new ExpenseExpiredException("Expense no longer updateable");
        }
        long budgetID=expenseDTO.getBudget().getId();
        double oldExpense = expense.getPrice();
        double newExpense = expenseDTO.getPrice();
        Budget actualBudget = budgetService.getBudget(budgetID);


        if(actualBudget.getBalance() >= expenseDTO.getBudget().getBalance()){

        if(oldExpense < newExpense) {
            double expenseIncrease = newExpense - oldExpense;
           actualBudget.setBalance(actualBudget.getBalance() - expenseIncrease);

        } else {

            double expenseDecrease = oldExpense - newExpense;
            if(expenseDecrease > actualBudget.getBalance()){
                try {
                    throw new AccountingConstraintViolationException(String.format("The Amount is lower the total balance"));
                } catch (AccountingConstraintViolationException e) {
                    throw new RuntimeException(e);
                }
            }

                else
            {
                actualBudget.setBalance(actualBudget.getBalance() + expenseDecrease);
            }
        }

        }
        else if(actualBudget.getBalance() < expenseDTO.getBudget().getBalance()){
            if(oldExpense < newExpense) {

                actualBudget.setBalance(expenseDTO.getBudget().getBalance() - newExpense);

            } else {


                if(newExpense > expenseDTO.getBudget().getBalance()){
                    try {
                        throw new AccountingConstraintViolationException(String.format("The Amount is lower the total balance"));
                    } catch (AccountingConstraintViolationException e) {
                        throw new RuntimeException(e);
                    }
                }

                else
                {
                    actualBudget.setBalance(expenseDTO.getBudget().getBalance() - oldExpense);
                }
            }
        }

        expense.setDescription(expenseDTO.getDescription());
        expense.setExpenseType(expenseDTO.getExpenseType());
        expense.setPrice(expenseDTO.getPrice());
        expense.setBudget(expenseDTO.getBudget());
        expenseRepository.save(expense);
        budgetRepository.save(actualBudget);
    }

    @Override
    public void deleteExpense(long expenseID) throws NoSuchExpenseOrNotAuthorized, ExpenseExpiredException, NoSuchEntityOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        long budgetID =mapToExpenseDTO(expenseRepository.findById(expenseID)).getBudget().getId();
        Budget actualBudget = budgetService.getBudget(budgetID);
       Expense expense = expenseRepository.findByIdAndWalletId(expenseID, loggedInUser.getWallet().getId());
        if(expense == null) {
            throw new NoSuchExpenseOrNotAuthorized("No such Expense or unauthorized");
        }
        ExpenseDTO expenseDTO = mapToExpenseDTO(expense);
        if(!isUpdateable(expenseDTO)){
            throw new ExpenseExpiredException("Expense no longer updateable");
        }
     actualBudget.setBalance(actualBudget.getBalance() + expenseDTO.getPrice());
        expenseRepository.delete(expense);
    }
    }
