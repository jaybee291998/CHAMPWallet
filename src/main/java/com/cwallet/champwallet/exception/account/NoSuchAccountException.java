package com.cwallet.champwallet.exception.account;

public class NoSuchAccountException extends Exception {
    public NoSuchAccountException(String s) {
        super(s);
    }

    public NoSuchAccountException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchAccountException(Throwable throwable) {
        super(throwable);
    }
}
