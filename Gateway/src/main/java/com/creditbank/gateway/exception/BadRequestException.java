package com.creditbank.gateway.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class BadRequestException extends RuntimeException {
    private final Map<String, String> errors;

    public BadRequestException(String message) {
        super(message);
        this.errors = null;
    }

    public BadRequestException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

}