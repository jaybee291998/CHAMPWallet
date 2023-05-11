package com.cwallet.champwallet.exception.account;

public class AccountAlreadyActivatedException extends Exception {
    public AccountAlreadyActivatedException(String s) {
        super(s);
    }

    public AccountAlreadyActivatedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AccountAlreadyActivatedException(Throwable throwable) {
        super(throwable);
    }
}
