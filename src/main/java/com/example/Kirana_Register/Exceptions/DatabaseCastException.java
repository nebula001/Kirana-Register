package com.example.Kirana_Register.Exceptions;

// Database-specific: Type casting error
public class DatabaseCastException extends RuntimeException {
    public DatabaseCastException(String message) {
        super(message);
    }
}
