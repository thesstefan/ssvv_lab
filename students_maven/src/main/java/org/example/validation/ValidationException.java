package org.example.validation;

public class ValidationException extends RuntimeException{
    public ValidationException(String exception) {
        super(exception);
    }
}

