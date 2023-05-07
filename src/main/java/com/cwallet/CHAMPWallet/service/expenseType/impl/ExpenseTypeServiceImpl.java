package com.cwallet.CHAMPWallet.service.expenseType.impl;

import com.cwallet.CHAMPWallet.dto.expenseType.ExpenseTypeDto;
import com.cwallet.CHAMPWallet.models.expenseType.ExpenseType;
import com.cwallet.CHAMPWallet.repository.expenseType.ExpenseTypeRepository;
import com.cwallet.CHAMPWallet.service.expenseType.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cwallet.CHAMPWallet.mappers.expenseType.ExpenseTypeMapper.mapToExpenseType;
@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {

    private ExpenseTypeRepository expenseTypeRepository;

    @Autowired
    public ExpenseTypeServiceImpl(ExpenseTypeRepository expenseTypeRepository){
        this.expenseTypeRepository = expenseTypeRepository;
    }
    @Override
    public void save(ExpenseTypeDto expenseTypeDto) {
        ExpenseType expense = mapToExpenseType(expenseTypeDto);
        ExpenseType newExpense = expenseTypeRepository.save(expense);

    }
}
