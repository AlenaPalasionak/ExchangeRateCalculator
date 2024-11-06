package org.example.exception;

public class ExcelSheetAccessException extends RuntimeException {
    public ExcelSheetAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

