package com.softindustry.bank.exception;

public class ZeroOrNegativeAmountException extends Exception {
    public ZeroOrNegativeAmountException(String message) {
        super(message);
    }
}
