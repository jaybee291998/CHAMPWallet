package com.cwallet.champwallet.exception.budget;

public class NoSuchBudgetOrNotAuthorized extends Exception{
    public NoSuchBudgetOrNotAuthorized(String s) {
        super(s);
    }

    public NoSuchBudgetOrNotAuthorized(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NoSuchBudgetOrNotAuthorized(Throwable throwable) {
        super(throwable);
    }
}
