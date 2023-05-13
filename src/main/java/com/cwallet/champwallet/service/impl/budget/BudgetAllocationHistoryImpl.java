package com.cwallet.champwallet.service.impl.budget;

import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryDTO;
import com.cwallet.champwallet.dto.budget.BudgetAllocationHistoryJson;
import com.cwallet.champwallet.dto.budget.BudgetDTO;
import com.cwallet.champwallet.exception.budget.NoSuchBudgetOrNotAuthorized;
import com.cwallet.champwallet.models.account.UserEntity;
import com.cwallet.champwallet.models.budget.Budget;
import com.cwallet.champwallet.models.budget.BudgetAllocationHistory;
import com.cwallet.champwallet.repository.budget.BudgetAllocationHistoryRepository;
import com.cwallet.champwallet.security.SecurityUtil;
import com.cwallet.champwallet.service.budget.BudgetAllocationHistoryService;
import com.cwallet.champwallet.service.budget.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cwallet.champwallet.mappers.budget.BudgetAllocationHistoryMapper.mapToBudgetAllocationHistoryDTO;
import static com.cwallet.champwallet.mappers.budget.BudgetAllocationHistoryMapper.mapToBudgetAllocationHistoryJson;

@Service
public class BudgetAllocationHistoryImpl implements BudgetAllocationHistoryService {
    @Autowired
    private BudgetAllocationHistoryRepository budgetAllocationHistoryRepository;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private BudgetService budgetService;
    @Override
    public List<BudgetAllocationHistoryJson> budgetAllocationHistory(long budgetId) {
        UserEntity loggedInUser = securityUtil.getLoggedInUser();
        BudgetDTO budget = null;
        try {
           budget = budgetService.getSpecificBudget(budgetId);
            return budgetAllocationHistoryRepository.findByBudgetId(budgetId).stream().map(e -> mapToBudgetAllocationHistoryJson(e)).collect(Collectors.toList());
        } catch (NoSuchBudgetOrNotAuthorized e) {
            return new ArrayList<BudgetAllocationHistoryJson>();
        }
    }
}
