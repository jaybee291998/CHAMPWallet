package com.cwallet.CHAMPWallet.exception.budget;

public class BudgetExpiredException extends Exception {
    public BudgetExpiredException(String s) {
        super(s);
    }

    public BudgetExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public BudgetExpiredException(Throwable throwable) {
        super(throwable);
    }
}
