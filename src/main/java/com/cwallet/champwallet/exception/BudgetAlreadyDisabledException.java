package com.cwallet.champwallet.exception;

public class BudgetAlreadyDisabledException extends Exception {
    public BudgetAlreadyDisabledException(String message) {
        super(message);
    }

    public BudgetAlreadyDisabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public BudgetAlreadyDisabledException(Throwable cause) {
        super(cause);
    }
}
