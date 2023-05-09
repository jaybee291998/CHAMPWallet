package com.cwallet.CHAMPWallet.exception.expenseType;

public class NoSuchExpenseTypeOrNotAuthorized extends Exception{

    public NoSuchExpenseTypeOrNotAuthorized(String s) {
        super(s);
    }

    public NoSuchExpenseTypeOrNotAuthorized(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchExpenseTypeOrNotAuthorized(Throwable throwable) {
        super(throwable);
    }
}
