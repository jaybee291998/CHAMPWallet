package com.cwallet.champwallet.exception.expense;

public class ExpenseExpiredException extends Exception{
    public ExpenseExpiredException(String s) {
        super(s);
    }

    public ExpenseExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ExpenseExpiredException(Throwable throwable) {
        super(throwable);
    }
}
