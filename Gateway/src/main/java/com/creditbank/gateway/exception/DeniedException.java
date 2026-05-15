package com.creditbank.gateway.exception;

public class DeniedException extends RuntimeException {
  public DeniedException(String message) {
    super(message);
  }
}
