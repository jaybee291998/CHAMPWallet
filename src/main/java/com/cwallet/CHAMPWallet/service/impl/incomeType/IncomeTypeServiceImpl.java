package com.cwallet.CHAMPWallet.service.impl.incomeType;

import com.cwallet.CHAMPWallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.CHAMPWallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.CHAMPWallet.mappers.incomeType.IncomeTypeMapper;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.models.budget.Budget;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import com.cwallet.CHAMPWallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.incomeType.IncomeTypeService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cwallet.CHAMPWallet.mappers.incomeType.IncomeTypeMapper.mapToIncomeTypeDto;

@Service
public class IncomeTypeServiceImpl implements IncomeTypeService {
    @Autowired
    private IncomeTypeRepository incomeTypeRepository;
    private SecurityUtil securityUtil;

    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;

    @Autowired
    public IncomeTypeServiceImpl(IncomeTypeRepository incomeTypeRepository, SecurityUtil securityUtil) {
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
    public List<IncomeTypeDto> getAllIncomeType() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<IncomeType> incomeType = incomeTypeRepository.findByWalletId(loggedInUser.getWallet().getId());
        return incomeType.stream().map(IncomeTypeMapper::mapToIncomeTypeDto).collect(Collectors.toList());
    }

    @Override
    public IncomeTypeDto getIncomeTypeById(long id) throws NoSuchBudgetOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        IncomeType incomeType = incomeTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());
        if (incomeType == null) {
            throw new NoSuchBudgetOrNotAuthorized("Not authorized or doesnt exsit");
        }
        return mapToIncomeTypeDto(incomeType);
    }

    @Override
    public IncomeType getIncomeType(long id) throws NoSuchBudgetOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        IncomeType incomeType = incomeTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());
        if (incomeType == null) {
            throw new NoSuchBudgetOrNotAuthorized("Not authorized or doesnt exsit");
        }
        return incomeType;
    }

    @Override
    public void update(IncomeTypeDto incomeTypeDto, long id) throws NoSuchBudgetOrNotAuthorized {

        if(incomeTypeDto == null) {
            throw new IllegalArgumentException("Income type dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        IncomeType incomeType = incomeTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());
        if(incomeType == null) {
            throw new NoSuchBudgetOrNotAuthorized("No such Income type or unauthorized");
        }
        incomeType.setName(incomeTypeDto.getName());
        incomeType.setDescription(incomeTypeDto.getDescription());
        incomeTypeRepository.save(incomeType);
    }


    @Override
    public boolean isUpdateable(IncomeTypeDto incomeTypeDto) {
        if(expirableAndOwnedService.isExpired(incomeTypeDto)) {
            return false;
        } else {
            return incomeTypeRepository.findByWalletId(incomeTypeDto.getId()).isEmpty();
        }
    }
}
