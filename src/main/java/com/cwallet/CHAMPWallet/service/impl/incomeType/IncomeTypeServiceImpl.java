package com.cwallet.CHAMPWallet.service.impl.incomeType;

import com.cwallet.CHAMPWallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.CHAMPWallet.mappers.incomeType.IncomeTypeMapper;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import com.cwallet.CHAMPWallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.incomeType.IncomeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeTypeServiceImpl implements IncomeTypeService {
    @Autowired
    private IncomeTypeRepository incomeTypeRepository;
    private SecurityUtil securityUtil;

    @Autowired
    public IncomeTypeServiceImpl(IncomeTypeRepository incomeTypeRepository, SecurityUtil securityUtil){
        this.incomeTypeRepository = incomeTypeRepository;
        this.securityUtil = securityUtil;
    }
    @Override
    public void save(IncomeTypeDto incomeTypeDto) {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Wallet newWallet = loggedInUser.getWallet();

        IncomeType incomeType = IncomeTypeMapper.mapToIncomeType(incomeTypeDto);
        incomeType.setWallet(newWallet);
        incomeTypeRepository.save(incomeType);

    }

    @Override
    public List<IncomeType> findAll() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Wallet newWallet = loggedInUser.getWallet();

        return incomeTypeRepository.findAll();
    }
}