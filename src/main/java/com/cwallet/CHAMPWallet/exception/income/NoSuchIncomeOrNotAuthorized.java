package com.cwallet.CHAMPWallet.exception.income;

public class NoSuchIncomeOrNotAuthorized extends Exception{
    public NoSuchIncomeOrNotAuthorized(String s) {
        super(s);
    }

    public NoSuchIncomeOrNotAuthorized(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchIncomeOrNotAuthorized(Throwable throwable) {
        super(throwable);
    }
}
