package com.cwallet.CHAMPWallet.service.expenseType.impl;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.CHAMPWallet.mappers.expenseType.ExpenseTypeMapper;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.expense.ExpenseType;
import com.cwallet.CHAMPWallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.expenseType.ExpenseTypeService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    @Override
    public void updateExpenseType(ExpenseTypeDto expenseTypeDto) {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Optional<ExpenseType> expenseTypeOptional = expenseTypeRepository.findById(expenseTypeDto.getId());

        if (!expenseTypeOptional.isPresent()) {
            try {
                throw new NoSuchExpenseTypeOrNotAuthorized("Expense type not found");
            } catch (NoSuchExpenseTypeOrNotAuthorized e) {
                throw new RuntimeException(e);
            }
        }

        ExpenseType expenseType = expenseTypeOptional.get();

        if (!expenseType.getWallet().getUser().equals(loggedInUser)) {
            try {
                throw new NoSuchExpenseTypeOrNotAuthorized("You have no rights to either access/use this resource");
            } catch (NoSuchExpenseTypeOrNotAuthorized e) {
                throw new RuntimeException(e);
            }
        }

        ExpenseType updatedExpenseType = mapToExpenseType(expenseTypeDto);
        updatedExpenseType.setWallet(expenseType.getWallet());

        expenseTypeRepository.save(updatedExpenseType);
    }
}
