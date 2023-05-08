package com.cwallet.CHAMPWallet.bean.expenseType;
import java.util.List;
import java.util.ArrayList;
public class ExpenseTypeListBean {
    private List<ExpenseTypeForm> expenseTypeList;

    public ExpenseTypeListBean(){
        expenseTypeList = new ArrayList<>();
    }

    public void addExpenseTypeForm(ExpenseTypeForm expenseTypeForm){
        expenseTypeList.add(expenseTypeForm);
    }
}
