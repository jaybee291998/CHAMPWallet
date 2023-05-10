package com.cwallet.CHAMPWallet.exception.income;

public class IncomeExpiredException extends Exception{
    public IncomeExpiredException(String s) {
        super(s);
    }

    public IncomeExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public IncomeExpiredException(Throwable throwable) {
        super(throwable);
    }
}
