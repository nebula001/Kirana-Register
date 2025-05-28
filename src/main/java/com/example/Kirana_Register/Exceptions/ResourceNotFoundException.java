package com.example.Kirana_Register.Exceptions;

// General: Resource not found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
