package com.cwallet.champwallet.service.impl.incomeType;

import com.cwallet.champwallet.dto.incomeType.IncomeTypeDto;
import com.cwallet.champwallet.exception.EntityExpiredException;
import com.cwallet.champwallet.exception.NoSuchEntityOrNotAuthorized;
import com.cwallet.champwallet.mappers.incomeType.IncomeTypeMapper;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.repository.income.IncomeRepository;
import com.cwallet.champwallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.incomeType.IncomeTypeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


import static com.cwallet.champwallet.mappers.incomeType.IncomeTypeMapper.mapToIncomeType;
import static com.cwallet.champwallet.mappers.incomeType.IncomeTypeMapper.mapToIncomeTypeDto;

@Service
public class IncomeTypeServiceImpl implements IncomeTypeService {
    @Autowired
    private IncomeTypeRepository incomeTypeRepository;


    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;
    @Autowired
    private IncomeRepository incomeRepository;

    @Override
    public boolean save(IncomeTypeDto incomeTypeDto) {
        IncomeType incomeType = mapToIncomeType(incomeTypeDto);
        incomeType.setWallet(securityUtil.getLoggedInUser().getWallet());

        try{
            incomeTypeRepository.save(incomeType);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public List<IncomeTypeDto> getAllIncomeType() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<IncomeType> incomeType = incomeTypeRepository.findByWalletId(loggedInUser.getWallet().getId());
        return incomeType.stream().map(IncomeTypeMapper::mapToIncomeTypeDto).collect(Collectors.toList());
    }

    @Override
    public IncomeTypeDto getIncomeTypeById(long id) throws NoSuchEntityOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        IncomeType incomeType = incomeTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());
        if (incomeType == null) {
            throw new NoSuchEntityOrNotAuthorized("Not authorized or doesnt exsit");
        }
        return mapToIncomeTypeDto(incomeType);
    }

    @Override
    public IncomeType getIncomeType(long id) throws NoSuchEntityOrNotAuthorized {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        IncomeType incomeType = incomeTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());
        if (incomeType == null) {
            throw new NoSuchEntityOrNotAuthorized("Not authorized or doesnt exsit");
        }
        return incomeType;
    }
    @Override
    public void update(IncomeTypeDto incomeTypeDto, long id) throws NoSuchEntityOrNotAuthorized {

        if(incomeTypeDto == null) {
            throw new IllegalArgumentException("Income type dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        IncomeType incomeType = incomeTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());
        if(incomeType == null) {
            throw new NoSuchEntityOrNotAuthorized("No such Income type or unauthorized");
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
            return incomeRepository.findByIncomeTypeId(incomeTypeDto.getId()).isEmpty();

        }
    }

    @Override
    public void deleteIncomeType(long id) throws NoSuchEntityOrNotAuthorized, EntityExpiredException {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        IncomeType incomeType = incomeTypeRepository.findByIdAndWalletId(id, loggedInUser.getWallet().getId());
        if(incomeType == null){
            throw new NoSuchEntityOrNotAuthorized("No such income type or unauthorized");
        }
        IncomeTypeDto incomeTypeDto = mapToIncomeTypeDto(incomeType);
        if (!isUpdateable (incomeTypeDto)){
            throw new EntityExpiredException("Income Type is no longer deletable");
        }
        incomeTypeRepository.delete(incomeType);
    }

}
