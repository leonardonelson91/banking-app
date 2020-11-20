package com.modularbank.account.exception;

public class CurrencyNotAvailableException extends RuntimeException {
    public CurrencyNotAvailableException(String message) {
        super(message);
    }
}
