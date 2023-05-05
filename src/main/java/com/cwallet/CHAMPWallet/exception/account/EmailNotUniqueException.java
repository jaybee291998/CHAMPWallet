package com.cwallet.CHAMPWallet.exception.account;

public class EmailNotUniqueException extends Exception{
    public EmailNotUniqueException(String s) {
        super(s);
    }

    public EmailNotUniqueException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EmailNotUniqueException(Throwable throwable) {
        super(throwable);
    }
}
