package com.cwallet.champwallet.exception;

public class BudgetAlreadyEnabledException extends Exception {
    public BudgetAlreadyEnabledException(String message) {
        super(message);
    }

    public BudgetAlreadyEnabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public BudgetAlreadyEnabledException(Throwable cause) {
        super(cause);
    }
}
