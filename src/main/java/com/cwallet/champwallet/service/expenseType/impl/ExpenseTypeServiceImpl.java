package com.cwallet.champwallet.service.expenseType.impl;

import com.cwallet.champwallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.champwallet.exception.expenseType.ExpenseTypeExpiredException;
import com.cwallet.champwallet.exception.expenseType.NoSuchExpenseTypeOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.expense.ExpenseType;
import com.cwallet.champwallet.repository.expense.ExpenseRepository;
import com.cwallet.champwallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.expenseType.ExpenseTypeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseType;
import static com.cwallet.champwallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseTypeDto;

@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {
    @Autowired
    private ExpenseTypeRepository expenseTypeRepository;
    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;

    @Autowired
    private ExpenseRepository expenseRepository;

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
    public void updateExpenseType(ExpenseTypeDto expenseTypeDTO, long expenseTypeId) throws NoSuchExpenseTypeOrNotAuthorized {
        if(expenseTypeDTO == null) {
            throw new IllegalArgumentException("expense type dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        ExpenseType expenseType = expenseTypeRepository.findByIdAndWalletId(expenseTypeId, loggedInUser.getWallet().getId());
        if(expenseType == null) {
            throw new NoSuchExpenseTypeOrNotAuthorized("No such expense type or unauthorized");
        }
        expenseType.setName(expenseTypeDTO.getName());
        expenseType.setDescription(expenseTypeDTO.getDescription());
        expenseTypeRepository.save(expenseType);
    }
    public boolean isUpdatable(ExpenseTypeDto expenseTypeDto){
        if(expirableAndOwnedService.isExpired(expenseTypeDto)){
            return  false;
        }
        else {
            return expenseRepository.findByExpenseTypeId(expenseTypeDto.getId()).isEmpty();
        }
    }

    public void deleteExpenseType(long expenseTypeId) throws NoSuchExpenseTypeOrNotAuthorized, ExpenseTypeExpiredException{
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        ExpenseType expenseType = expenseTypeRepository.findByIdAndWalletId(expenseTypeId, loggedInUser.getWallet().getId());

        if(expenseType == null){
            throw new NoSuchExpenseTypeOrNotAuthorized("No such expense type or not authorized");
        }

        ExpenseTypeDto expenseTypeDto = mapToExpenseTypeDto(expenseType);
        if(!isUpdatable(expenseTypeDto)){
            throw new ExpenseTypeExpiredException("Expense Type no longer updatable");
        }
        expenseTypeRepository.delete(expenseType);
    }
}
