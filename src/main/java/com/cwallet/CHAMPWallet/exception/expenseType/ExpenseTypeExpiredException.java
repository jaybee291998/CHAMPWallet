package com.cwallet.CHAMPWallet.exception.expenseType;

public class ExpenseTypeExpiredException extends Exception{
    public ExpenseTypeExpiredException(String s){super(s);}

    public ExpenseTypeExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ExpenseTypeExpiredException(Throwable throwable) {
        super(throwable);
    }
}
