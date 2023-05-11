package com.cwallet.CHAMPWallet.exception;

public class NoSuchEntityOrNotAuthorized extends Exception{
    public NoSuchEntityOrNotAuthorized(String s) {
        super(s);
    }

    public NoSuchEntityOrNotAuthorized(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchEntityOrNotAuthorized(Throwable throwable) {
        super(throwable);
    }
}
