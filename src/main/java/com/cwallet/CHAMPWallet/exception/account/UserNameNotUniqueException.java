package com.cwallet.CHAMPWallet.exception.account;

public class UserNameNotUniqueException extends Exception{
    public UserNameNotUniqueException(String s) {
        super(s);
    }

    public UserNameNotUniqueException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public UserNameNotUniqueException(Throwable throwable) {
        super(throwable);
    }
}
