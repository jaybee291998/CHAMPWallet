package com.cwallet.champwallet.exception.expense;

public class NoSuchExpenseOrNotAuthorized extends Exception{
    public NoSuchExpenseOrNotAuthorized(String s) {
        super(s);
    }

    public NoSuchExpenseOrNotAuthorized(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchExpenseOrNotAuthorized(Throwable throwable) {
        super(throwable);
    }
}
