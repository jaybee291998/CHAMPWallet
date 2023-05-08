package com.cwallet.CHAMPWallet.service.expenseType.impl;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import com.cwallet.CHAMPWallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.expenseType.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.CHAMPWallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseType;
@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {
    private ExpenseTypeRepository expenseTypeRepository;
    private SecurityUtil securityUtil;
    @Autowired
    public ExpenseTypeServiceImpl(ExpenseTypeRepository expenseTypeRepository, SecurityUtil securityUtil){
        this.expenseTypeRepository = expenseTypeRepository;
        this.securityUtil = securityUtil;
    }
    @Override
    public void save(ExpenseTypeDto expenseTypeDto) {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Wallet newWallet = loggedInUser.getWallet();

        ExpenseType expense = mapToExpenseType(expenseTypeDto);
        expense.setWallet(newWallet);
        expense.setEnabled(true);
        expenseTypeRepository.save(expense);
    }

    public List<ExpenseType> findAll(ExpenseTypeDto expenseTypeDto){
        return expenseTypeRepository.findAll();
    }

}
