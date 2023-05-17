package com.cwallet.champwallet.exception;

public class EntityExpiredException extends Exception {
    public EntityExpiredException(String s) {
        super(s);
    }

    public EntityExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public EntityExpiredException(Throwable throwable) {
        super(throwable);
    }
}
