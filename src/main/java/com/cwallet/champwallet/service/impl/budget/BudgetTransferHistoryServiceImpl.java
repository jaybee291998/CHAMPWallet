package com.cwallet.champwallet.service.impl.budget;

import com.cwallet.champwallet.dto.budget.BudgetTransferHistoryDTO;
import com.cwallet.champwallet.repository.budget.BudgetTransferHistoryRepository;
import com.cwallet.champwallet.service.budget.BudgetTransferHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cwallet.champwallet.mappers.budget.BudgetTransferHistoryMapper.mapToBudgetTransferHistory;
@Service
public class BudgetTransferHistoryServiceImpl implements BudgetTransferHistoryService {
    @Autowired
    private BudgetTransferHistoryRepository budgetTransferHistoryRepository;
    @Override
    public void save(BudgetTransferHistoryDTO budgetTransferHistoryDTO) {
        if(budgetTransferHistoryDTO == null) {
            throw new IllegalArgumentException("transfer history DTO must not be null");
        }
        budgetTransferHistoryRepository.save(mapToBudgetTransferHistory(budgetTransferHistoryDTO));
    }
}
