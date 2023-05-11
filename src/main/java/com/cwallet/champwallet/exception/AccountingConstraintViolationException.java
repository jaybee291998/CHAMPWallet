package com.cwallet.champwallet.exception;

public class AccountingConstraintViolationException extends Exception {
    public AccountingConstraintViolationException(String s) {
        super(s);
    }

    public AccountingConstraintViolationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AccountingConstraintViolationException(Throwable throwable) {
        super(throwable);
    }
}
