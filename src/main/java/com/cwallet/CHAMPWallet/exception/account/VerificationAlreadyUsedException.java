package com.cwallet.CHAMPWallet.exception.account;

public class VerificationAlreadyUsedException extends Exception {
    public VerificationAlreadyUsedException(String s) {
        super(s);
    }

    public VerificationAlreadyUsedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public VerificationAlreadyUsedException(Throwable throwable) {
        super(throwable);
    }
}
