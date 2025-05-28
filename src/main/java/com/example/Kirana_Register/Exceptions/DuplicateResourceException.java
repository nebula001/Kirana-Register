package com.example.Kirana_Register.Exceptions;

// Database-specific: Duplicate key or constraint violation
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}

