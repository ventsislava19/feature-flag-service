package com.featureflags.exception;

// Thrown when a flag is not found by name or id.
public class FlagNotFoundException extends RuntimeException{

    public FlagNotFoundException(String message) {
        super(message);
    }
}
