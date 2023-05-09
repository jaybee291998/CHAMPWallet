package com.cwallet.CHAMPWallet.service.expenseType.impl;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.CHAMPWallet.mappers.expenseType.ExpenseTypeMapper;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import com.cwallet.CHAMPWallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.expenseType.ExpenseTypeService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.CHAMPWallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseType;
import static com.cwallet.CHAMPWallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseTypeDto;

@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {
    @Autowired
    private ExpenseTypeRepository expenseTypeRepository;
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Override
    public boolean save(ExpenseTypeDto expenseTypeDto) {
        ExpenseType expense = mapToExpenseType(expenseTypeDto);
        expense.setWallet(securityUtil.getLoggedInUser().getWallet());
        expense.setEnabled(true);
        try{
            expenseTypeRepository.save(expense);
            return true;
        }catch (Exception e){
           return false;
        }
    }
    public List<ExpenseTypeDto> getAllUserExpenseType(){
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<ExpenseType> usersExpenseType = expenseTypeRepository.findByWalletId(loggedInUser.getWallet().getId());
        return usersExpenseType.stream().map(expenseType -> mapToExpenseTypeDto(expenseType)).collect(Collectors.toList());
    }

    @Override
    public ExpenseTypeDto getExpenseTypeId(long id) throws NoSuchExpenseTypeOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        ExpenseType expenseType = expenseTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());

        if(expenseType == null){
            throw new NoSuchExpenseTypeOrNotAuthorized("Not authorized or doesn't exist");
        }
        ExpenseTypeDto expenseTypeDto = mapToExpenseTypeDto(expenseType);
        return expenseTypeDto;
    }


}
