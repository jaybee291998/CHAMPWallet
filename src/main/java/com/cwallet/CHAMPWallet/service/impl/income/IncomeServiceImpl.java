package com.cwallet.CHAMPWallet.service.impl.income;

import com.cwallet.CHAMPWallet.repository.budget.BudgetRepository;
import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.account.Wallet;
import com.cwallet.CHAMPWallet.models.income.Income;
import com.cwallet.CHAMPWallet.models.income.IncomeType;
import com.cwallet.CHAMPWallet.repository.account.WalletRepository;
import com.cwallet.CHAMPWallet.repository.income.IncomeRepository;
import com.cwallet.CHAMPWallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.income.IncomeService;
import com.cwallet.CHAMPWallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import com.cwallet.CHAMPWallet.exception.income.NoSuchIncomeOrNotAuthorized;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cwallet.CHAMPWallet.mappers.budget.BudgetMapper.mapToBudgetDTO;
import static com.cwallet.CHAMPWallet.mappers.income.IncomeMapper.mapToIncome;
import static com.cwallet.CHAMPWallet.mappers.income.IncomeMapper.mapToIncomeDTO;
@Service
public class IncomeServiceImpl implements IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private IncomeTypeRepository incomeTypeRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private ExpirableAndOwnedService expirableAndOwnedService;


    @Override
    @Transactional
    public boolean save(IncomeDTO incomeDTO, String incomeTypeIDStr) {
        Long incomeTypeID = Long.valueOf(incomeTypeIDStr);
        Optional<IncomeType> incomeType = incomeTypeRepository.findById(incomeTypeID);
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
       Income income = mapToIncome(incomeDTO);
        income.setWallet(wallet);
//        incomeDTO.setWallet();
        income.setIncomeType(incomeType.get());
        wallet.setBalance(wallet.getBalance() + income.getAmount());
        try {
            incomeRepository.save(income);
            walletRepository.save(wallet);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    @Override
    public List<IncomeDTO> getAllUserIncome() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<Income> usersIncome = incomeRepository.findByWalletId(loggedInUser.getWallet().getId());
        return usersIncome.stream().map((income) -> mapToIncomeDTO(income)).collect(Collectors.toList());
    }

//@Override
//    public IncomeDTO getSpecificIncome(long incomeID) throws NoSuchIncomeOrNotAuthorized {
//        UserEntity loggedInUser = securityUtil.getLoggedInUser();
//        Income income= incomeRepository.findByIdAndWalletId(incomeID, loggedInUser.getWallet().getId());
//        if(income == null) {
//            throw new NoSuchIncomeOrNotAuthorized("Not authorized or doesnt exsit");
//        }
//        IncomeDTO incomeDTO = mapToIncomeDTO(income);
//        return incomeDTO;
//    }
@Override
public IncomeDTO getSpecificBudget(long incomeID) throws NoSuchIncomeOrNotAuthorized {
    UserEntity loggedInUser = securityUtil.getLoggedInUser();
    Income income = incomeRepository.findByIdAndWalletId(incomeID, loggedInUser.getWallet().getId());
    if(income == null) {
        throw new NoSuchIncomeOrNotAuthorized("Not authorized or doesnt exsit");
    }
   IncomeDTO incomeDTO = mapToIncomeDTO(income);
    return incomeDTO;
}

    @Override
    public void update(IncomeDTO incomeDTO, long incomeID) throws NoSuchIncomeOrNotAuthorized {
        if(incomeDTO == null) {
            throw new IllegalArgumentException("budget dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Income income = incomeRepository.findByIdAndWalletId(incomeID, loggedInUser.getWallet().getId());
        if(income == null) {
            throw new NoSuchIncomeOrNotAuthorized("No such income or unauthorized");
        }
        income.setSourceOfIncome(incomeDTO.getSource());
        income.setDescription(incomeDTO.getDescription());
        income.setAmount(incomeDTO.getAmount());
        income.setIncomeType(incomeDTO.getIncomeType());
        incomeRepository.save(income);
    }

    @Override
    public boolean isUpdateable(IncomeDTO incomeDTO){
        if(expirableAndOwnedService.isExpired(incomeDTO)) {
            return false;
        } else {
          if(incomeDTO.getAmount()<0){
              throw
          }
        }
    }
}
