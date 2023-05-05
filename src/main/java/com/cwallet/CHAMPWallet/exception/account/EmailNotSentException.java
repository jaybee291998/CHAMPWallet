package com.cwallet.CHAMPWallet.exception.account;

public class EmailNotSentException extends Exception {
    public EmailNotSentException(String s) {
        super(s);
    }

    public EmailNotSentException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EmailNotSentException(Throwable throwable) {
        super(throwable);
    }
}
