package com.featureflags.exception;

// Thrown when creating or renaming a flag to a name already in use.
public class DuplicateFlagNameException extends RuntimeException{

    public DuplicateFlagNameException(String message) {
        super(message);
    }
}
