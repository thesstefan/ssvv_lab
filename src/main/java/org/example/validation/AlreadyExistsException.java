package org.example.validation;

public class AlreadyExistsException extends Exception {
    public AlreadyExistsException(String exception) {
        super(exception);
    }
}
