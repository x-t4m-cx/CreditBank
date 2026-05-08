package com.creditbank.gateway.exception;

public class StatementNotFoundException extends RuntimeException {
    public StatementNotFoundException(String message) {
        super(message);
    }
}
