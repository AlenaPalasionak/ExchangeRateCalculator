package org.example.exception;

public class GoogleSheetsAccessException extends RuntimeException {
    public GoogleSheetsAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}