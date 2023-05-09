package com.cwallet.CHAMPWallet.service.impl.income;

import com.cwallet.CHAMPWallet.dto.income.IncomeDTO;
import com.cwallet.CHAMPWallet.models.account.UserEntity;
import com.cwallet.CHAMPWallet.models.income.Income;
import com.cwallet.CHAMPWallet.repository.income.IncomeRepository;
import com.cwallet.CHAMPWallet.security.SecurityUtil;
import com.cwallet.CHAMPWallet.service.income.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.CHAMPWallet.mappers.income.IncomeMapper.mapToIncomeDTO;
@Service
public class IncomeServiceImpl implements IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public List<IncomeDTO> getAllUserIncome() {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        List<Income> usersIncome = incomeRepository.findByWalletId(loggedInUser.getWallet().getId());
        return usersIncome.stream().map((income) -> mapToIncomeDTO(income)).collect(Collectors.toList());
    }
}
