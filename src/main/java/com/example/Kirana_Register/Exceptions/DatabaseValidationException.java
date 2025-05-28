package com.example.Kirana_Register.Exceptions;

// Database-specific: Invalid data format or validation
public class DatabaseValidationException extends RuntimeException {
    public DatabaseValidationException(String message) {
        super(message);
    }
}
