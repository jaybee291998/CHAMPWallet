package com.cwallet.champwallet.exception;

public class BudgetDisabledException extends Exception {
    public BudgetDisabledException(String message) {
        super(message);
    }

    public BudgetDisabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public BudgetDisabledException(Throwable cause) {
        super(cause);
    }
}
