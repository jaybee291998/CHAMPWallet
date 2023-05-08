package com.cwallet.CHAMPWallet.bean.expenseType;

import java.util.ArrayList;
import java.util.List;

public class ExpenseTypeListBean {
    private List<ExpenseTypeForm> expenseTypeList;

    public ExpenseTypeListBean(){
        expenseTypeList = new ArrayList<>();
    }

    public void addExpenseTypeForm(ExpenseTypeForm expenseTypeForm){
        expenseTypeList.add(expenseTypeForm);
    }
}
