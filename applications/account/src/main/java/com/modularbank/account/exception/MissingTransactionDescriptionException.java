package com.modularbank.account.exception;

public class MissingTransactionDescriptionException extends RuntimeException {
    public MissingTransactionDescriptionException(String message) {
        super(message);
    }
}
