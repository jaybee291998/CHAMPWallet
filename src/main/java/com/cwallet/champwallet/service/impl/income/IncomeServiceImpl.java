package com.cwallet.champwallet.service.impl.income;

import com.cwallet.champwallet.dto.income.IncomeDTO;
import com.cwallet.champwallet.exception.AccountingConstraintViolationException;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.income.Income;
import com.cwallet.champwallet.models.income.IncomeType;
import com.cwallet.champwallet.repository.account.WalletRepository;
import com.cwallet.champwallet.repository.income.IncomeRepository;
import com.cwallet.champwallet.repository.incomeType.IncomeTypeRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.income.IncomeService;
import com.cwallet.champwallet.utils.ExpirableAndOwnedService;
import org.springframework.beans.factory.annotation.Autowired;
import com.cwallet.champwallet.exception.income.NoSuchIncomeOrNotAuthorized;
import com.cwallet.champwallet.exception.income.IncomeExpiredException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.income.IncomeMapper.mapToIncome;
import static com.cwallet.champwallet.mappers.income.IncomeMapper.mapToIncomeDTO;
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
    public boolean save(IncomeDTO incomeDTO, IncomeType incomeType) {
        Long incomeTypeID = incomeType.getId();
        Optional<IncomeType> incomeTypes = incomeTypeRepository.findById(incomeTypeID);
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
       Income income = mapToIncome(incomeDTO);
        income.setWallet(wallet);
//        incomeDTO.setWallet();
        income.setIncomeType(incomeTypes.get());
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


@Override
public IncomeDTO getSpecificIncome(long incomeID) throws NoSuchIncomeOrNotAuthorized {
    UserEntity loggedInUser = securityUtil.getLoggedInUser();
    Income income = incomeRepository.findByIdAndWalletId(incomeID, loggedInUser.getWallet().getId());
    if(income == null) {
        throw new NoSuchIncomeOrNotAuthorized("Not authorized or doesnt exsit");
    }
   IncomeDTO incomeDTO = mapToIncomeDTO(income);
    return incomeDTO;
}

    @Override
    public void update(IncomeDTO incomeDTO, long incomeID) throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException,AccountingConstraintViolationException {
        if(incomeDTO == null) {
            throw new IllegalArgumentException("budget dto must not be null");
        }
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Income income = incomeRepository.findByIdAndWalletId(incomeID, loggedInUser.getWallet().getId());
        if(income == null) {
            throw new NoSuchIncomeOrNotAuthorized("No such income or unauthorized");
        }
        if(!isUpdateable(incomeDTO)){
            throw new IncomeExpiredException("Income no longer updateable");
        }
        double oldIncome = income.getAmount();
        double newIncome = incomeDTO.getAmount();
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        // income increase
        if(oldIncome < newIncome) {
            double incomeIncrease = newIncome - oldIncome;
            wallet.setBalance(wallet.getBalance() + incomeIncrease) ;

        } else {
            // income decrease
            double incomeDecrease = oldIncome - newIncome;
            if(incomeDecrease > wallet.getBalance()){
                try {
                    throw new AccountingConstraintViolationException(String.format("The Amount is lower the total balance"));
                } catch (AccountingConstraintViolationException e) {
                    throw new RuntimeException(e);
                }
            }else
            {
                wallet.setBalance(wallet.getBalance() - incomeDecrease);
            }
        }
        income.setSourceOfIncome(incomeDTO.getSource());
        income.setDescription(incomeDTO.getDescription());
        income.setAmount(incomeDTO.getAmount());
        income.setIncomeType(incomeDTO.getIncomeType());
        incomeRepository.save(income);
    }

    @Override
    public boolean isUpdateable(IncomeDTO incomeDTO){
//        List<IncomeDTO> userIncome = incomeService.getAllUserIncome();
//        double totalAmount = userIncome.stream().reduce(0D, (subtotal, element) -> subtotal + element.getAmount(), Double::sum);
        if(expirableAndOwnedService.isExpired(incomeDTO)) {
            return false;
        }
//        if(incomeDTO.getAmount() < (totalAmount-(securityUtil.getLoggedInUser().getWallet().getBalance())))
//        {
//            return false;
//        }

        return true;
    }

    @Override
    public void deleteIncome(long incomeID) throws NoSuchIncomeOrNotAuthorized, IncomeExpiredException {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        Income income = incomeRepository.findByIdAndWalletId(incomeID, loggedInUser.getWallet().getId());
        if(income == null) {
            throw new NoSuchIncomeOrNotAuthorized("No such Income or unauthorized");
        }
        IncomeDTO incomeDTO = mapToIncomeDTO(income);
        if(!isUpdateable(incomeDTO)){
            throw new IncomeExpiredException("Income no longer updateable");
        }
        wallet.setBalance(wallet.getBalance()-incomeDTO.getAmount());
        incomeRepository.delete(income);
    }
}
