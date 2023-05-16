package com.cwallet.champwallet.service.impl.budget;

import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryDTO;
import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryJson;
import com.cwallet.champwallet.models.account.Wallet;
import com.cwallet.champwallet.models.budget.BudgetTransferHistory;
import com.cwallet.champwallet.repository.budget.BudgetTransferHistoryRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetTransferHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.budget.BudgetTransferHistoryMapper.mapToBudgetTransferHistory;
import static com.cwallet.champwallet.mappers.budget.BudgetTransferHistoryMapper.mapToBudgetTransferHistoryJson;

@Service
public class BudgetTransferHistoryServiceImpl implements BudgetTransferHistoryService {
    @Autowired
    private BudgetTransferHistoryRepository budgetTransferHistoryRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Override
    public void save(BudgetTransferHistoryDTO budgetTransferHistoryDTO) {
        if(budgetTransferHistoryDTO == null) {
            throw new IllegalArgumentException("transfer history DTO must not be null");
        }
        budgetTransferHistoryRepository.save(mapToBudgetTransferHistory(budgetTransferHistoryDTO));
    }

    @Override
    public List<BudgetTransferHistoryJson> budgetTransferredTo(long senderBudgetID, int intervalInDays) {
        if(intervalInDays <= 0) {
            throw new IllegalArgumentException("Interval must be greater than 0");
        }
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(intervalInDays);
        List<BudgetTransferHistory> history = budgetTransferHistoryRepository.budgetTransferredTo(senderBudgetID, start, end, wallet.getId());
        return history.stream().map(h -> mapToBudgetTransferHistoryJson(h)).collect(Collectors.toList());
    }

    @Override
    public List<BudgetTransferHistoryJson> budgetReceivedFrom(long recipientBudgetID, int intervalInDays) {
        if(intervalInDays <= 0) {
            throw new IllegalArgumentException("Interval must be greater than 0");
        }
        Wallet wallet = securityUtil.getLoggedInUser().getWallet();
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(intervalInDays);
        List<BudgetTransferHistory> history = budgetTransferHistoryRepository.budgetReceivedFrom(recipientBudgetID, start, end, wallet.getId());
        return history.stream().map(h -> mapToBudgetTransferHistoryJson(h)).collect(Collectors.toList());
    }
}
